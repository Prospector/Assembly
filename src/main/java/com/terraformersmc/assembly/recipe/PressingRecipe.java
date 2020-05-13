package com.terraformersmc.assembly.recipe;

import com.terraformersmc.assembly.item.AssemblyItems;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.util.recipe.InputValidator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PressingRecipe extends AssemblyRecipe<Inventory> implements InputValidator {
	public Ingredient input;
	public ItemStack output;
	public int presses;

	public PressingRecipe(Identifier id, Ingredient input, ItemStack output, int presses) {
		super(id, false);
		this.input = input;
		this.output = output;
		this.presses = presses;
	}

	@Override
	public boolean isValidInput(ItemStack stack) {
		return this.input.test(stack);
	}

	public PressingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean recipeMatches(Inventory inv, World world) {
		return this.isValidInput(inv.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return this.output.copy();
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyItems.PRESS);
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.PRESSING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.PRESSING;
	}
}
