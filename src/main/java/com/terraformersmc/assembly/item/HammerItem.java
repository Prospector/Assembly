package com.terraformersmc.assembly.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import reborncore.common.recipes.ExtendedRecipeRemainder;

public class HammerItem extends PickaxeItem implements ExtendedRecipeRemainder {

	public HammerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}

	@Override
	public ItemStack getRemainderStack(ItemStack stack) {
		stack.damage(1, RANDOM, null);
		if (stack.getDamage() >= stack.getMaxDamage()) {
			return ItemStack.EMPTY;
		}
		return stack;
	}
}
