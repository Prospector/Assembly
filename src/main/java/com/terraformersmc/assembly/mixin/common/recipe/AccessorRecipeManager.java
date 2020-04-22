package com.terraformersmc.assembly.mixin.common.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface AccessorRecipeManager {
	@Accessor
	Map<RecipeType<?>, Map<Identifier, Recipe<?>>> getRecipes();
}