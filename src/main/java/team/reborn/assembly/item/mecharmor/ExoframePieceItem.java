package team.reborn.assembly.item.mecharmor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.util.AssemblyConstants;
import team.reborn.assembly.util.ItemUtil;

public class ExoframePieceItem extends ArmorItem {
	public static final ArmorMaterial EXOFRAME_MATERIAL = new EmptyMechanicalArmorMaterial();
	private static final String MODULE_KEY = AssemblyConstants.NbtKeys.MODULE;

	public ExoframePieceItem(EquipmentSlot slot, Settings settings) {
		super(EXOFRAME_MATERIAL, slot, settings);
	}

	public ItemStack setModule(ItemStack piece, ItemStack module) {
		ItemStack oldModule = ItemStack.EMPTY;
		CompoundTag tag = piece.getOrCreateTag();
		if (tag.contains(MODULE_KEY)) {
			oldModule = ItemUtil.stackFromTag(tag);
		}
		ItemUtil.stackToTag(module, tag);
		piece.setTag(tag);
		return oldModule;
	}

	public static class EmptyMechanicalArmorMaterial implements ArmorMaterial {

		@Override
		public int getDurability(EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return 0;
		}

		@Override
		public int getEnchantability() {
			return 11;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(AssemblyItems.BRASS_PLATE);
		}

		@Override
		public String getName() {
			return "empty_mechanical";
		}

		@Override
		public float getToughness() {
			return 0.0F;
		}
	}
}
