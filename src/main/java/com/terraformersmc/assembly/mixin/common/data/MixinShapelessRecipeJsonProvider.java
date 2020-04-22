package com.terraformersmc.assembly.mixin.common.data;

import com.terraformersmc.assembly.Assembly;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapelessRecipeJsonFactory.ShapelessRecipeJsonProvider.class)
public class MixinShapelessRecipeJsonProvider {
	@Shadow
	@Final
	private Item output;

	@Shadow
	@Final
	private Identifier advancementId;

	@Inject(method = "getAdvancementId", at = @At(value = "HEAD"), cancellable = true)
	private void modifyAdvancementId(CallbackInfoReturnable<Identifier> info) {
		if (advancementId.getNamespace().equals(Assembly.MOD_ID) && Assembly.DOSSIER_ENABLED) {
			info.setReturnValue(new Identifier(advancementId.getNamespace(), advancementId.getPath().replace("recipes/" + this.output.getGroup().getName() + "/", "recipes/")));
		}
	}
}
