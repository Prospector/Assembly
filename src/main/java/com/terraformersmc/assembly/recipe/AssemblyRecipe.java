package com.terraformersmc.assembly.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class AssemblyRecipe<BE extends Inventory> implements Recipe<BE> {
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
	public final boolean matches(BE inv, World world) {
		return !this.isDummy() && this.recipeMatches(inv, world);
	}

	protected abstract boolean recipeMatches(BE inv, World world);

	private boolean isDummy() {
		return this.dummy;
	}

	@Override
	public ItemStack craft(BE inv) {
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
		return this.id;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	public boolean isIgnoredByPacketSender() {
		return false;
	}
}
