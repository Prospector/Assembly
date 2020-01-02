package team.reborn.assembly.recipe;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import io.github.cottonmc.libcd.api.CustomOutputRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import team.reborn.assembly.recipe.provider.BoilingRecipeProvider;
import team.reborn.assembly.recipe.serializer.AssemblyRecipeSerializers;

import java.util.Collection;
import java.util.Collections;

public class BoilingRecipe extends AssemblyRecipe<BoilingRecipeProvider> implements CustomOutputRecipe {
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

	@Override
	public Collection<Item> getOutputItems() {
		return Collections.emptySet();
	}

}
