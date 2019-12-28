package team.reborn.assembly.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import team.reborn.assembly.recipe.provider.PressingRecipeProvider;

public abstract class PressingRecipe implements Recipe<PressingRecipeProvider> {
	protected final RecipeType<?> type;
	public Identifier id;
	public Ingredient input;
	public ItemStack output;
	public int presses;

	public PressingRecipe(RecipeType<?> type, Identifier id, Ingredient input, ItemStack output, int presses) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.presses = presses;
		this.type = type;
	}

	@Override
	public boolean matches(PressingRecipeProvider inv, World world) {
		return this.input.test(inv.getInvStack(0));
	}

	@Override
	public ItemStack craft(PressingRecipeProvider inv) {
		return this.output.copy();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		return output;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeType<?> getType() {
		return type;
	}
}
