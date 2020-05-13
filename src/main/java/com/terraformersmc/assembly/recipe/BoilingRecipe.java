package com.terraformersmc.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.recipe.provider.FluidInputInventory;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.util.recipe.CustomRecipeToastIcon;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BoilingRecipe extends AssemblyRecipe<FluidInputInventory> implements CustomRecipeToastIcon {
	public Fluid inputFluid;
	public FluidAmount ratio;
	public Fluid output;
	public final FluidFilter input = fluidKey -> fluidKey.getRawFluid() != null && fluidKey.getRawFluid().equals(inputFluid);

	public BoilingRecipe(Identifier id, Fluid input, FluidAmount ratio, Fluid output) {
		super(id);
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
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyBlocks.BOILER);
	}

	@Override
	public ItemStack getRecipeToastIcon() {
		if (output != null) {
			return new ItemStack(output.getBucketItem());
		}
		return ItemStack.EMPTY;
	}
}
