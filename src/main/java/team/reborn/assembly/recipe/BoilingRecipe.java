package team.reborn.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import team.reborn.assembly.recipe.provider.BoilingRecipeProvider;
import team.reborn.assembly.recipe.serializer.AssemblyRecipeSerializers;

public class BoilingRecipe implements Recipe<BoilingRecipeProvider> {
	public Identifier id;
	public Fluid input;
	public float ratio;
	public Fluid output;

	public BoilingRecipe(Identifier id, Fluid input, float ratio, Fluid output) {
		this.id = id;
		this.input = input;
		this.ratio = ratio;
		this.output = output;
	}

	@Override
	public boolean matches(BoilingRecipeProvider inv, World world) {
		FluidVolume fluidInput = inv.getFluidInput();
		return fluidInput.getRawFluid() == this.input && !fluidInput.getAmount_F().isZero();
	}

	@Override
	public ItemStack craft(BoilingRecipeProvider inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.BOILING;
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.BOILING;
	}
}
