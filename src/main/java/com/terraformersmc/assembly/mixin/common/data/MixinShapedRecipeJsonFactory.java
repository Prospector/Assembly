package com.terraformersmc.assembly.mixin.common.data;

import com.terraformersmc.assembly.Assembly;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ShapedRecipeJsonFactory.class)
public class MixinShapedRecipeJsonFactory {
	@ModifyVariable(method = "offerTo(Ljava/util/function/Consumer;Lnet/minecraft/util/Identifier;)V", at = @At(value = "HEAD"))
	private Identifier modifyRecipeId(Identifier recipeId) {
		if (recipeId.getNamespace().equals(Assembly.MOD_ID) && Assembly.DOSSIER_ENABLED) {
			return new Identifier(recipeId.getNamespace(), "crafting/" + recipeId.getPath());
		}
		return recipeId;
	}
}
