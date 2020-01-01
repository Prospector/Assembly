package team.reborn.assembly.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public class MechanicalArmorItem extends ArmorItem {
	public MechanicalArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	public static class Material implements ArmorMaterial {
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
			return 0;
		}

		@Override
		public SoundEvent getEquipSound() {
			return null;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public float getToughness() {
			return 0;
		}
	}
}
