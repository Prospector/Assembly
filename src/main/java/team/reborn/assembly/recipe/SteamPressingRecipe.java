package team.reborn.assembly.recipe;

import io.github.cottonmc.libcd.api.CustomOutputRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.recipe.provider.PressingRecipeProvider;
import team.reborn.assembly.recipe.serializer.AssemblyRecipeSerializers;

public class SteamPressingRecipe extends AssemblyRecipe<PressingRecipeProvider> {
	public Ingredient input;
	public ItemStack output;
	public int presses;

	public SteamPressingRecipe(Identifier id, Ingredient input, ItemStack output, int presses) {
		super(id, false);
		this.input = input;
		this.output = output;
		this.presses = presses;
	}

	public SteamPressingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean recipeMatches(PressingRecipeProvider inv, World world) {
		return this.input.test(inv.getInvStack(0));
	}

	@Override
	public ItemStack craft(PressingRecipeProvider inv) {
		return this.output.copy();
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyItems.STEAM_PRESS);
	}

	@Override
	public RecipeType<?> getType() {
		return AssemblyRecipeTypes.STEAM_PRESSING;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.STEAM_PRESSING;
	}
}
