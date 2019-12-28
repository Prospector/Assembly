package team.reborn.assembly.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.recipe.serializer.AssemblyRecipeSerializers;

public class SteamPressingRecipe extends PressingRecipe {

	public SteamPressingRecipe(Identifier id, Ingredient input, ItemStack output, int presses) {
		super(AssemblyRecipeTypes.STEAM_PRESSING, id, input, output, presses);
	}

	@Override
	public ItemStack getRecipeKindIcon() {
		return new ItemStack(AssemblyItems.STEAM_PRESS);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AssemblyRecipeSerializers.STEAM_PRESSING;
	}
}
