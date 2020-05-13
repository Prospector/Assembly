package com.terraformersmc.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.util.recipe.CustomRecipeToastIcon;
import com.terraformersmc.assembly.util.recipe.InputValidator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SqueezingRecipe extends AssemblyRecipe<Inventory> implements InputValidator, CustomRecipeToastIcon {
	public Ingredient input;
	public FluidVolume output;
	public int squeezes;

	public SqueezingRecipe(Identifier id, Ingredient input, FluidVolume output, int squeezes) {
		super(id, false);
		this.input = input;
		this.output = output;
		this.squeezes = squeezes;
	}

	public SqueezingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean isValidInput(ItemStack stack) {
		return this.input.test(stack);
	}

	@Override
	public boolean recipeMatches(Inventory inv, World world) {
		return this.isValidInput(inv.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv) {
		return null;
	}

	@Override
	public ItemStack getOutput() {
		return null;
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyBlocks.SQUEEZER);
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.SQUEEZING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.SQUEEZING;
	}

	@Override
	public ItemStack getRecipeToastIcon() {
		if (output != null && output.getRawFluid() != null && output.getRawFluid().getBucketItem() != null) {
			return new ItemStack(output.getRawFluid().getBucketItem());
		}
		return ItemStack.EMPTY;
	}
}
