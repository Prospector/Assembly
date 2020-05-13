package com.terraformersmc.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.blockentity.InjectorBlockEntity;
import com.terraformersmc.assembly.recipe.ingredient.FluidIngredient;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.util.recipe.InputValidator;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class InjectingRecipe extends AssemblyRecipe<InjectorBlockEntity> implements InputValidator {
	public Ingredient itemInput;
	public FluidIngredient fluidInput;
	public ItemStack output;

	public InjectingRecipe(Identifier id, Ingredient itemInput, FluidIngredient fluidInput, ItemStack output) {
		super(id, false);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.output = output;
	}

	public InjectingRecipe(Identifier id) {
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
	public boolean recipeMatches(InjectorBlockEntity inv, World world) {
		boolean validItem = this.isValidInput(inv.getStack(0));
		boolean validFluid = this.isValidInput(inv.getTank().getInvFluid(0));
		return validItem && validFluid;
	}

	@Override
	public ItemStack craft(InjectorBlockEntity inv) {
		return this.output.copy();
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyBlocks.INJECTOR);
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.INJECTING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.INJECTING;
	}
}
