package com.terraformersmc.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import com.terraformersmc.assembly.recipe.provider.FluidInputInventory;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.util.recipe.CustomRecipeToastIcon;
import io.github.cottonmc.libcd.api.CustomOutputRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;

public class BoilingRecipe extends AssemblyRecipe<FluidInputInventory> implements CustomOutputRecipe, CustomRecipeToastIcon {
	private FluidIngredient input;
	private FluidVolume output;

	public BoilingRecipe(Identifier id, FluidIngredient input, FluidVolume output) {
		super(id);
		this.input = input;
		this.output = output;
	}

	public BoilingRecipe(Identifier id) {
		super(id, true);
	}

	public boolean isValidInput(FluidVolume volume) {
		return this.input.test(volume);
	}

	public FluidIngredient getInputIngredient() {
		return input;
	}

	public FluidVolume getOutputVolume() {
		return output;
	}

	@Override
	public boolean recipeMatches(FluidInputInventory inv, World world) {
		return this.isValidInput(inv.getFluidInput());
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.BOILING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.BOILING;
	}

	@Override
	public Collection<Item> getOutputItems() {
		return Collections.emptySet();
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyBlocks.BOILER);
	}

	@Override
	public ItemStack getRecipeToastIcon() {
		Fluid fluid = output.getRawFluid();
		if (fluid != null) {
			return new ItemStack(fluid.getBucketItem());
		}
		return ItemStack.EMPTY;
	}
}
