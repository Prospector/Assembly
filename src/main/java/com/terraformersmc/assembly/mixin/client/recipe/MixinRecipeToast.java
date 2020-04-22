package com.terraformersmc.assembly.mixin.client.recipe;

import com.terraformersmc.assembly.util.recipe.CustomRecipeToastIcon;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeToast.class)
public class MixinRecipeToast {
	@Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Recipe;getOutput()Lnet/minecraft/item/ItemStack;"))
	private ItemStack modifyToastIcon(Recipe<?> recipe) {
		return recipe instanceof CustomRecipeToastIcon ? ((CustomRecipeToastIcon) recipe).getRecipeToastIcon() : recipe.getOutput();
	}
}
