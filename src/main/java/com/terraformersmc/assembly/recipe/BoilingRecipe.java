package com.terraformersmc.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.recipe.provider.FluidInputInventory;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import io.github.cottonmc.libcd.api.CustomOutputRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;

public class BoilingRecipe extends AssemblyRecipe<FluidInputInventory> implements CustomOutputRecipe {
	public FluidFilter input;
	public Fluid inputFluid;
	public FluidAmount ratio;
	public Fluid output;

	public BoilingRecipe(Identifier id, Fluid input, FluidAmount ratio, Fluid output) {
		super(id);
		this.input = fluidKey -> fluidKey.getRawFluid() != null && fluidKey.getRawFluid().equals(input);
		this.inputFluid = input;
		this.ratio = ratio;
		this.output = output;
	}

	public BoilingRecipe(Identifier id) {
		super(id, true);
	}

	@Override
	public boolean recipeMatches(FluidInputInventory inv, World world) {
		FluidVolume fluidInput = inv.getFluidInput();
		return this.input.matches(fluidInput.getFluidKey()) && !fluidInput.getAmount_F().isZero();
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

}
