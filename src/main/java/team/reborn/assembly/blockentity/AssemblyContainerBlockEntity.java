package team.reborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import team.reborn.assembly.util.ItemUtils;

public abstract class AssemblyContainerBlockEntity extends LockableContainerBlockEntity implements Inventory {

	protected DefaultedList<ItemStack> contents = DefaultedList.ofSize(getInvSize(), ItemStack.EMPTY);

	public AssemblyContainerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void fromTag(CompoundTag compound) {
		Inventories.fromTag(compound, contents);
		super.fromTag(compound);
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		Inventories.toTag(compound, contents, true);
		return super.toTag(compound);
	}

	@Override
	public boolean isInvEmpty() {
		return contents.stream().anyMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return contents.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return Inventories.splitStack(this.contents, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return Inventories.removeStack(this.contents, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.contents.set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}

	@Override
	public void onInvOpen(PlayerEntity player) {

	}

	@Override
	public void onInvClose(PlayerEntity player) {

	}

	@Override
	public boolean isValidInvStack(int i, ItemStack stack) {
		return true;
	}

	@Override
	public abstract Container createContainer(int syncId, PlayerInventory inventory);

	@Override
	public void clear() {
		contents.clear();
	}
}
