package team.reborn.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import team.reborn.assembly.recipe.provider.BoilingRecipeProvider;
import team.reborn.assembly.recipe.serializer.AssemblyRecipeSerializers;

public class BoilingRecipe extends AssemblyRecipe<BoilingRecipeProvider> {
	public Fluid input;
	public FluidAmount ratio;
	public Fluid output;

	public BoilingRecipe(Identifier id, Fluid input, FluidAmount ratio, Fluid output) {
		super(id);
		this.input = input;
		this.ratio = ratio;
		this.output = output;
	}

	public BoilingRecipe(Identifier id) {
		super(id, true);
	}

	@Override
	public boolean recipeMatches(BoilingRecipeProvider inv, World world) {
		FluidVolume fluidInput = inv.getFluidInput();
		return fluidInput.getRawFluid() == this.input && !fluidInput.getAmount_F().isZero();
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.BOILING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.BOILING;
	}

}
