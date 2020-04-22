package com.terraformersmc.assembly.mixin.common.data;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.util.recipe.RecipeUtil;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CookingRecipeJsonFactory.class)
public class MixinCookingRecipeJsonFactory {
	@Shadow
	@Final
	private CookingRecipeSerializer<?> serializer;

	@ModifyVariable(method = "offerTo(Ljava/util/function/Consumer;Lnet/minecraft/util/Identifier;)V", at = @At(value = "HEAD"))
	private Identifier modifyRecipeId(Identifier recipeId) {
		if (recipeId.getNamespace().equals(Assembly.MOD_ID) && Assembly.DOSSIER_ENABLED) {
			return new Identifier(recipeId.getNamespace(), RecipeUtil.getSmeltingRecipeType(serializer) + recipeId.getPath());
		}
		return recipeId;
	}
}
