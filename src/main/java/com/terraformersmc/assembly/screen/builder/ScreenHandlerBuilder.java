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

package com.terraformersmc.assembly.screen.builder;

import com.terraformersmc.assembly.blockentity.base.AssemblyContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ScreenHandlerBuilder {

	private final Identifier name;

	private Predicate<PlayerEntity> canInteract = player -> true;

	final List<Slot> slots;
	final LinkedList<Tank> tanks;
	final List<Range<Integer>> inventorySlotRange, blockEntitySlotRange;

	final List<Pair<Supplier, Consumer>> objectValues;

	final List<Consumer<CraftingInventory>> craftEvents;

	final int width, height;

	TextPositioner inventoryTitlePositioner = null, titlePositioner;

	public ScreenHandlerBuilder(final Identifier name, int width, int height, TextPositioner titlePositioner) {
		this.name = name;
		this.width = width;
		this.height = height;

		this.slots = new ArrayList<>();
		this.tanks = new LinkedList<>();
		this.inventorySlotRange = new ArrayList<>();
		this.blockEntitySlotRange = new ArrayList<>();

		this.objectValues = new ArrayList<>();

		this.craftEvents = new ArrayList<>();

		this.titlePositioner = titlePositioner;
	}

	public ScreenHandlerBuilder(final Identifier name, int width, int height) {
		this(name, width, height, (w, h, text, textRenderer) -> new ScreenPos((w - textRenderer.getStringWidth(text)) / 2, 6));
	}

	public ScreenHandlerBuilder interact(final Predicate<PlayerEntity> canInteract) {
		this.canInteract = canInteract;
		return this;
	}

	public ScreenHandlerInventoryBuilder player(final PlayerEntity player) {
		return new ScreenHandlerInventoryBuilder(this, player);
	}

	public ScreenHandlerContainerBuilder container(final Inventory container) {
		return new ScreenHandlerContainerBuilder(this, container);
	}

	void addPlayerInventorySlotRange(final Range<Integer> range) {
		this.inventorySlotRange.add(range);
	}

	void addContainerSlotRange(final Range<Integer> range) {
		this.blockEntitySlotRange.add(range);
	}

	@Deprecated
	/**
	 * The container have to know if the BlockEntity is still available (the block was not destroyed)
	 * and if the player is not to far from him to close the GUI if necessary
	 */
	public ScreenSyncer create(int syncId) {
		final ScreenSyncer built = new ScreenSyncer(this.name, this.canInteract, this.inventorySlotRange, this.blockEntitySlotRange, null, width, height, syncId);

		if (!this.objectValues.isEmpty())
			built.addObjectSync(this.objectValues);
		if (!this.craftEvents.isEmpty())
			built.addCraftEvents(this.craftEvents);

		built.titlePositioner = this.titlePositioner;
		built.inventoryTitlePositioner = this.inventoryTitlePositioner;

		this.slots.forEach(built::addSlot);
		this.tanks.forEach(built::addTank);

		this.slots.clear();
		this.tanks.clear();
		return built;
	}

	public ScreenSyncer create(final AssemblyContainerBlockEntity blockEntity, int syncId) {
		final ScreenSyncer built = new ScreenSyncer(this.name, this.canInteract, this.inventorySlotRange, this.blockEntitySlotRange, blockEntity, width, height, syncId);

		if (!this.craftEvents.isEmpty()) {
			built.addCraftEvents(this.craftEvents);
		}
		if (!this.objectValues.isEmpty()) {
			built.addObjectSync(this.objectValues);
		}

		built.titlePositioner = this.titlePositioner;
		built.inventoryTitlePositioner = this.inventoryTitlePositioner;

		this.slots.forEach(built::addSlot);
		this.tanks.forEach(built::addTank);

		this.slots.clear();
		this.tanks.clear();
		return built;
	}
}
