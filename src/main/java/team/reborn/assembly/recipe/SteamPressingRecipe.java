package team.reborn.assembly.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.recipe.serializer.AssemblyRecipeSerializers;

public class SteamPressingRecipe extends AssemblyRecipe<Inventory> {
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
	public boolean recipeMatches(Inventory inv, World world) {
		return this.input.test(inv.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv) {
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
