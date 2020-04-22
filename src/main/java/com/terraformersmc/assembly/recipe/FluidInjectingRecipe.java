package com.terraformersmc.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.blockentity.FluidInjectorBlockEntity;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.util.recipe.InputValidator;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FluidInjectingRecipe extends AssemblyRecipe<FluidInjectorBlockEntity> implements InputValidator {
	public Ingredient itemInput;
	public FluidIngredient fluidInput;
	public ItemStack output;

	public FluidInjectingRecipe(Identifier id, Ingredient itemInput, FluidIngredient fluidInput, ItemStack output) {
		super(id, false);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.output = output;
	}

	public FluidInjectingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean isValidInput(ItemStack stack) {
		return this.itemInput.test(stack);
	}

	@Override
	public boolean isValidInput(FluidVolume stack) {
		return this.fluidInput.test(stack);
	}

	@Override
	public boolean recipeMatches(FluidInjectorBlockEntity inv, World world) {
		return this.isValidInput(inv.getStack(0)) && this.isValidInput(inv.getTank().getInvFluid(0));
	}

	@Override
	public ItemStack craft(FluidInjectorBlockEntity inv) {
		return this.output.copy();
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyBlocks.FLUID_INJECTOR);
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.FLUID_INJECTING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.FLUID_INJECTING;
	}
}
