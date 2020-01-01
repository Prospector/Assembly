package team.reborn.assembly.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public abstract class AssemblyRecipe<C extends Inventory> implements Recipe<C> {
	private final Identifier id;
	private final boolean dummy;

	public AssemblyRecipe(Identifier id, boolean dummy) {
		this.id = id;
		this.dummy = dummy;
	}

	public AssemblyRecipe(Identifier id) {
		this(id, false);
	}

	@Override
	public final boolean matches(C inv, World world) {
		return !isDummy() && recipeMatches(inv, world);
	}

	protected abstract boolean recipeMatches(C inv, World world);

	private boolean isDummy() {
		return dummy;
	}

	@Override
	public ItemStack craft(C inv) {
		throw new UnsupportedOperationException("Recipe '" + id + "' of type '" + Registry.RECIPE_TYPE.getId(getType()) + "' does not support Recipe#craft");
	}

	@Override
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getOutput() {
		throw new UnsupportedOperationException("Recipe '" + id + "' of type '" + Registry.RECIPE_TYPE.getId(getType()) + "' does not support Recipe#getOutput");
	}

	@Override
	public Identifier getId() {
		return id;
	}
}
