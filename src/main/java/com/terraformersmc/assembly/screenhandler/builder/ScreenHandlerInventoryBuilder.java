/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.terraformersmc.assembly.screenhandler.builder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.Range;

public final class ScreenHandlerInventoryBuilder {

	private final PlayerEntity player;
	private final ScreenHandlerBuilder parent;
	private Range<Integer> main;
	private Range<Integer> hotbar;
	private Range<Integer> armor;

	ScreenHandlerInventoryBuilder(final ScreenHandlerBuilder parent, final PlayerEntity player) {
		this.player = player;
		this.parent = parent;
	}

	public ScreenHandlerInventoryBuilder inventory(final int xStart, final int yStart) {
		final int startIndex = this.parent.slots.size();
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.parent.slots.add(new Slot(this.player.inventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
		this.main = Range.between(startIndex, this.parent.slots.size() - 1);
		return this;
	}

	public ScreenHandlerInventoryBuilder hotbar(final int xStart, final int yStart) {
		final int startIndex = this.parent.slots.size();
		for (int i = 0; i < 9; ++i)
			this.parent.slots.add(new Slot(this.player.inventory, i, xStart + i * 18, yStart));
		this.hotbar = Range.between(startIndex, this.parent.slots.size() - 1);
		return this;
	}

	public ScreenHandlerInventoryBuilder inventory() {
		return this.inventory(8, 94);
	}

	public ScreenHandlerInventoryBuilder hotbar() {
		return this.hotbar(8, 152);
	}

	public ContainerPlayerArmorInventoryBuilder armor() {
		return new ContainerPlayerArmorInventoryBuilder(this);
	}

	public ScreenHandlerBuilder addInventory() {
		if (this.hotbar != null)
			this.parent.addPlayerInventoryRange(this.hotbar);
		if (this.main != null)
			this.parent.addPlayerInventoryRange(this.main);
		if (this.armor != null)
			this.parent.addBlockEntityRange(this.armor);

		return this.parent;
	}

	public static final class ContainerPlayerArmorInventoryBuilder {
		private final ScreenHandlerInventoryBuilder parent;
		private final int startIndex;

		public ContainerPlayerArmorInventoryBuilder(final ScreenHandlerInventoryBuilder parent) {
			this.parent = parent;
			this.startIndex = parent.parent.slots.size();
		}

//		private ContainerPlayerArmorInventoryBuilder armor(final int index, final int xStart, final int yStart,
//		                                                   final EntityEquipmentSlot slotType, final String sprite) {
//			this.parent.parent.slots.add(new SpriteSlot(this.parent.player, index, xStart, yStart, sprite, 1)
//					.setFilter(stack -> stack.getItem().isValidArmor(stack, slotType, this.parent.player.player)));
//			return this;
//		}

//		public ContainerPlayerArmorInventoryBuilder helmet(final int xStart, final int yStart) {
//			return this.armor(this.parent.player.getSizeInventory() - 2, xStart, yStart, EntityEquipmentSlot.HEAD, IconSupplier.armour_head_name);
//		}
//
//		public ContainerPlayerArmorInventoryBuilder chestplate(final int xStart, final int yStart) {
//			return this.armor(this.parent.player.getSizeInventory() - 3, xStart, yStart, EntityEquipmentSlot.CHEST, IconSupplier.armour_chest_name);
//		}
//
//		public ContainerPlayerArmorInventoryBuilder leggings(final int xStart, final int yStart) {
//			return this.armor(this.parent.player.getSizeInventory() - 4, xStart, yStart, EntityEquipmentSlot.LEGS, IconSupplier.armour_legs_name);
//		}
//
//		public ContainerPlayerArmorInventoryBuilder boots(final int xStart, final int yStart) {
//			return this.armor(this.parent.player.getSizeInventory() - 5, xStart, yStart, EntityEquipmentSlot.FEET, IconSupplier.armour_feet_name);
//		}

//		public ContainerPlayerArmorInventoryBuilder complete(final int xStart, final int yStart) {
//			return this.helmet(xStart, yStart).chestplate(xStart, yStart + 18).leggings(xStart, yStart + 18 + 18)
//				.boots(xStart, yStart + 18 + 18 + 18);
//		}

		public ScreenHandlerInventoryBuilder addArmor() {
			this.parent.armor = Range.between(this.startIndex, this.parent.parent.slots.size() - 1);
			return this.parent;
		}
	}
}
