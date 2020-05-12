package com.terraformersmc.assembly.screen.builder;

import alexiil.mc.lib.attributes.fluid.FluidInvUtil;
import com.terraformersmc.assembly.mixin.common.screenhandler.ScreenHandlerListenersAccessor;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import com.terraformersmc.assembly.util.ItemUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ScreenSyncer<BE extends BlockEntity & Nameable> extends ScreenHandler implements ExtendedScreenHandlerListener {

	private final Identifier id;

	private final Predicate<PlayerEntity> canInteract;
	private final List<Range<Integer>> inventorySlotRange;
	private final List<Range<Integer>> blockEntitySlotRange;

	public final LinkedList<Tank> tanks;
	private final ArrayList<MutableTriple<Supplier, Consumer, Object>> objectValues;
	private List<Consumer<CraftingInventory>> craftEvents;

	private final BE blockEntity;

	private int width, height;

	TextPositioner inventoryTitlePositioner = null, titlePositioner = null;

	public ScreenSyncer(final Identifier id, final Predicate<PlayerEntity> canInteract,
						final List<Range<Integer>> inventorySlotRange,
						final List<Range<Integer>> blockEntitySlotRange, BE blockEntity, int width, int height, int syncId) {
		super(null, syncId);
		this.id = id;

		this.canInteract = canInteract;

		this.inventorySlotRange = inventorySlotRange;
		this.blockEntitySlotRange = blockEntitySlotRange;

		this.objectValues = new ArrayList<>();
		this.tanks = new LinkedList<>();

		this.blockEntity = blockEntity;

		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public Slot addSlot(Slot slot) {
		return super.addSlot(slot);
	}

	public Tank addTank(Tank tank) {
		this.tanks.add(tank);
		return tank;
	}

	public void addObjectSync(final List<Pair<Supplier, Consumer>> syncables) {
		for (final Pair<Supplier, Consumer> syncable : syncables) {
			this.objectValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), null));
		}
		this.objectValues.trimToSize();
	}

	public void addCraftEvents(final List<Consumer<CraftingInventory>> craftEvents) {
		this.craftEvents = craftEvents;
	}

	@Override
	public boolean canUse(final PlayerEntity player) {
		return this.canInteract.test(player);
	}

	@Override
	public final void onContentChanged(final Inventory container) {
		if (!this.craftEvents.isEmpty())
			this.craftEvents.forEach(consumer -> consumer.accept((CraftingInventory) container));
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
		for (final ScreenHandlerListener listener : ((ScreenHandlerListenersAccessor) (this)).getListeners()) {
			sync(listener, false);
		}
	}

	public void sync(ScreenHandlerListener listener, boolean force) {
		if (!this.objectValues.isEmpty()) {
			int objects = 0;
			for (final MutableTriple<Supplier, Consumer, Object> value : this.objectValues) {
				final Object currentValue = value.getLeft().get();
				Object lastValue = value.getRight();
				if (force || !currentValue.equals(lastValue)) {
					this.sendObject(listener, this, objects, currentValue);
					value.setRight(currentValue);
				}
				objects++;
			}
		}
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
		sync(listener, false);
	}

	public void clickTank(Tank tank) {
		AssemblyNetworking.clickTank(tanks.indexOf(tank));
	}

	public void onTankClick(ServerPlayerEntity player, int tankIndex) {
		Tank tank = tanks.get(tankIndex);
		FluidInvUtil.interactCursorWithTank(tank.fluidContainer.getPureInsertable().filtered(tank.getInsertFilter()), tank.fluidContainer.getPureExtractable().filtered(tank.getExtractFilter()), player);
	}

	@Override
	public void handleObject(int i, Object value) {
		this.objectValues.get(i).getMiddle().accept(value);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack originalStack = ItemStack.EMPTY;

		final Slot slot = this.slots.get(index);

		if (slot != null && slot.hasStack()) {

			final ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();

			boolean shifted = false;

			for (final Range<Integer> range : this.inventorySlotRange)
				if (range.contains(index)) {

					if (this.shiftToBlockEntity(stackInSlot))
						shifted = true;
					break;
				}

			if (!shifted)
				for (final Range<Integer> range : this.blockEntitySlotRange)
					if (range.contains(index)) {
						if (this.shiftToInventory(stackInSlot))
							shifted = true;
						break;
					}

			slot.onStackChanged(stackInSlot, originalStack);
			if (stackInSlot.getCount() <= 0)
				slot.setStack(ItemStack.EMPTY);
			else
				slot.markDirty();
			if (stackInSlot.getCount() == originalStack.getCount())
				return ItemStack.EMPTY;
			slot.onTakeItem(player, stackInSlot);
		}
		return originalStack;
	}

	protected boolean shiftItemStack(final ItemStack stackToShift, final int start, final int end) {
		boolean changed = false;
		if (stackToShift.isStackable()) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				final Slot slot = this.slots.get(slotIndex);
				final ItemStack stackInSlot = slot.getStack();
				if (!stackInSlot.isEmpty() && ItemUtil.isItemEqual(stackInSlot, stackToShift, true, true)
						&& slot.canInsert(stackToShift)) {
					final int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
					final int max = Math.min(stackToShift.getMaxCount(), slot.getMaxStackAmount());
					if (resultingStackSize <= max) {
						stackToShift.setCount(0);
						stackInSlot.setCount(resultingStackSize);
						slot.markDirty();
						changed = true;
					} else if (stackInSlot.getCount() < max) {
						stackToShift.decrement(max - stackInSlot.getCount());
						stackInSlot.setCount(max);
						slot.markDirty();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.getCount() > 0) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				final Slot slot = this.slots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot.isEmpty() && slot.canInsert(stackToShift)) {
					final int max = Math.min(stackToShift.getMaxCount(), slot.getMaxStackAmount());
					stackInSlot = stackToShift.copy();
					stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
					stackToShift.decrement(stackInSlot.getCount());
					slot.setStack(stackInSlot);
					slot.markDirty();
					changed = true;
				}
			}
		}
		return changed;
	}

	private boolean shiftToBlockEntity(final ItemStack stackToShift) {
		for (final Range<Integer> range : this.blockEntitySlotRange)
			if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1))
				return true;
		return false;
	}

	private boolean shiftToInventory(final ItemStack stackToShift) {
		for (final Range<Integer> range : this.inventorySlotRange)
			if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1))
				return true;
		return false;
	}

	public Identifier getId() {
		return this.id;
	}

	public BE getBlockEntity() {
		return this.blockEntity;
	}

	public TextPositioner getInventoryTitlePositioner() {
		return inventoryTitlePositioner;
	}

	public TextPositioner getTitlePositioner() {
		return titlePositioner;
	}
}
