package team.reborn.assembly.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public abstract class AssemblyContainerBlockEntity extends LockableContainerBlockEntity implements Inventory {

	protected DefaultedList<ItemStack> contents = DefaultedList.ofSize(size(), ItemStack.EMPTY);

	public AssemblyContainerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		Inventories.fromTag(tag, contents);
		super.fromTag(state, tag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		Inventories.toTag(compound, contents, true);
		return super.toTag(compound);
	}

	@Override
	public boolean isEmpty() {
		return contents.stream().anyMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getStack(int slot) {
		return contents.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(this.contents, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.contents, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.contents.set(slot, stack);
	}

	@Override
	public int getMaxCountPerStack() {
		return 64;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onOpen(PlayerEntity player) {

	}

	@Override
	public void onClose(PlayerEntity player) {

	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public abstract ScreenHandler createContainer(int syncId, PlayerInventory inventory);

	@Override
	public void clear() {
		contents.clear();
	}
}
