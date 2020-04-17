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
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import com.terraformersmc.assembly.blockentity.AssemblyContainerBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ScreenHandlerBuilder {

	private final Identifier name;

	private Predicate<PlayerEntity> canInteract = player -> true;

	final List<Slot> slots;
	final List<Range<Integer>> inventoryRange, blockEntityRange;

	final List<Pair<Supplier, Consumer>> objectValues;

	final List<Consumer<CraftingInventory>> craftEvents;

	public ScreenHandlerBuilder(final Identifier name) {

		this.name = name;

		this.slots = new ArrayList<>();
		this.inventoryRange = new ArrayList<>();
		this.blockEntityRange = new ArrayList<>();

		this.objectValues = new ArrayList<>();

		this.craftEvents = new ArrayList<>();
	}

	public ScreenHandlerBuilder interact(final Predicate<PlayerEntity> canInteract) {
		this.canInteract = canInteract;
		return this;
	}

	public ScreenHandlerInventoryBuilder player(final PlayerEntity player) {
		return new ScreenHandlerInventoryBuilder(this, player);
	}

	public ScreenHandlerContainerBuilder blockEntity(final Inventory container) {
		return new ScreenHandlerContainerBuilder(this, container);
	}

	void addPlayerInventoryRange(final Range<Integer> range) {
		this.inventoryRange.add(range);
	}

	void addBlockEntityRange(final Range<Integer> range) {
		this.blockEntityRange.add(range);
	}

	@Deprecated
	/**
	 * The container have to know if the BlockEntity is still available (the fluidBlock was not destroyed)
	 * and if the player is not to far from him to close the GUI if necessary
	 */
	public BuiltScreenHandler create(int syncId) {
		final BuiltScreenHandler built = new BuiltScreenHandler(this.name, this.canInteract,
				this.inventoryRange,
				this.blockEntityRange, null, syncId);
		if (!this.objectValues.isEmpty())
			built.addObjectSync(objectValues);
		if (!this.craftEvents.isEmpty())
			built.addCraftEvents(this.craftEvents);

		this.slots.forEach(built::addSlot);

		this.slots.clear();
		return built;
	}

	public BuiltScreenHandler create(final AssemblyContainerBlockEntity blockEntity, int syncId) {
		final BuiltScreenHandler built = new BuiltScreenHandler(this.name, this.canInteract,
				this.inventoryRange,
				this.blockEntityRange, blockEntity, syncId);
		if (!this.craftEvents.isEmpty())
			built.addCraftEvents(this.craftEvents);
		if (!this.objectValues.isEmpty())
			built.addObjectSync(objectValues);

		this.slots.forEach(built::addSlot);

		this.slots.clear();
		return built;
	}
}
