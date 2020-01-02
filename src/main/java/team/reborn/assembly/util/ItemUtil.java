package team.reborn.assembly.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class ItemUtil {

	public static boolean isItemEqual(final ItemStack a, final ItemStack b, final boolean matchDamage,
									  final boolean matchNBT) {
		if (a.isEmpty() || b.isEmpty())
			return false;
		if (a.getItem() != b.getItem())
			return false;
		if (matchNBT && !ItemStack.areItemsEqualIgnoreDamage(a, b))
			return false;
		if (matchDamage) {
			if (a.getDamage() != b.getDamage())
				return false;
		}
		return true;
	}

	public static boolean isItemEqual(ItemStack a, ItemStack b, boolean matchDamage, boolean matchNBT,
									  boolean useTags) {
		if (isItemEqual(a, b, matchDamage, matchNBT)) {
			return true;
		}
		if (a.isEmpty() || b.isEmpty())
			return false;
		if (useTags) {
			//TODO check tags
		}
		return false;
	}

	public static void contentsToTag(Inventory inv, String tag, CompoundTag data) {
		ListTag list = new ListTag();
		for (byte slot = 0; slot < inv.getInvSize(); slot++) {
			ItemStack stack = inv.getInvStack(slot);
			if (!stack.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", slot);
				stackToTag(stack, itemTag);
				list.add(itemTag);
			}
		}
		data.put(tag, list);
	}

	public static void contentsFromTag(Inventory inv, String tag, CompoundTag data) {
		ListTag list = data.getList(tag, 10);
		for (byte entry = 0; entry < list.size(); entry++) {
			CompoundTag itemTag = list.getCompound(entry);
			int slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < inv.getInvSize()) {
				ItemStack stack = stackFromTag(itemTag);
				inv.setInvStack(slot, stack);
			}
		}
	}

	public static void stackToTag(ItemStack stack, CompoundTag tag) {
		if (stack.isEmpty() || stack.getCount() <= 0)
			return;
		stack.toTag(tag);
	}

	public static ItemStack stackFromTag(CompoundTag tag) {
		return ItemStack.fromTag(tag);
	}

}
