package com.terraformersmc.assembly.mixin.common.data;

import com.terraformersmc.assembly.Assembly;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CookingRecipeJsonFactory.CookingRecipeJsonProvider.class)
public class MixinCookingRecipeJsonProvider {
	@Shadow
	@Final
	private Identifier advancementId;

	@Shadow
	@Final
	private Item result;

	@Inject(method = "getAdvancementId", at = @At(value = "HEAD"), cancellable = true)
	private void modifyAdvancementId(CallbackInfoReturnable<Identifier> info) {
		if (advancementId.getNamespace().equals(Assembly.MOD_ID) && Assembly.DOSSIER_ENABLED) {
			info.setReturnValue(new Identifier(advancementId.getNamespace(), advancementId.getPath().replace("recipes/" + this.result.getGroup().getName() + "/", "recipes/")));
		}
	}
}
