package com.terraformersmc.assembly.util;

import com.google.common.collect.ImmutableSet;
import com.terraformersmc.assembly.item.AssemblyItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import com.terraformersmc.assembly.block.propertyenum.ValveState;

import java.util.Set;
import java.util.function.Supplier;

public class AssemblyConstants {
	public static final String MOD_ID = "assembly";
	public static final String COMMON_NAMESPACE = "c";
	public static final Set<String> AUTHORS = ImmutableSet.of("9927b75a-89d9-4ff7-8bac-58aaed08911a");

	public static class NbtKeys {
		public static final String ITEM_STACK = "ItemStack";
		public static final String FLUIDS = "Fluids";
		public static final String INPUT_FLUIDS = "InputFluids";
		public static final String OUTPUT_FLUIDS = "OutputFluids";

		public static final String RECIPE = "Recipe";

		//Fluid Hopper & Spigot
		public static final String TRANSFER_COOLDOWN = "TransferCooldown";
		public static final String WORLD_PULL_COOLDOWN = "WorldPullCooldown";
		public static final String CUSTOM_NAME = "CustomName";

		//Spigot
		public static final String POURING_FLUID = "PouringFluid";

		//Steam Press
		public static final String BURN_TIME = "BurnTime";
		public static final String FUEL_TIME = "BurnTime";
		public static final String PRESS_PROGRESS = "Progress";
		public static final String PRESS_RESET = "Reset";
		public static final String CURRENT_PRESSES = "CurrentPresses";
		public static final String MASTER = "Master";

		//Piston Boots
		public static final String EXTENDED = "Extended";
	}

	public static class Ids {
		public static final Identifier BOILER = id("boiler");
		public static final Identifier BOILER_CHAMBER = id("boiler_chamber");
		public static final Identifier FLUID_HOPPER = id("fluid_hopper");
		public static final Identifier TINKERING_TABLE = id("tinkering_table");
		public static final Identifier SQUEEZER = id("squeezer");
		public static final Identifier INJECTOR = id("injector");

		public static final Identifier AUTHOR_CAPE = id("textures/entity/player/author_cape.png");
		public static final Identifier CONTRIBUTOR_CAPE = id("textures/entity/player/contributor_cape.png");

		public static final Identifier STEAM_TANK = id("steam_tank");
		public static final Identifier EXOFRAME = id("exoframe");

		public static final Identifier SALT_DOME = id("salt_dome");

	}

	public static class Properties {
		// Hevea Log
		public static final BooleanProperty ALIVE = BooleanProperty.of("alive");
		public static final BooleanProperty NORTH_LATEX = BooleanProperty.of("north_latex");
		public static final BooleanProperty SOUTH_LATEX = BooleanProperty.of("south_latex");
		public static final BooleanProperty WEST_LATEX = BooleanProperty.of("west_latex");
		public static final BooleanProperty EAST_LATEX = BooleanProperty.of("east_latex");

		// Tree Tap & Spigot
		public static final BooleanProperty POURING = BooleanProperty.of("pouring");

		// Spigot
		public static final EnumProperty<ValveState> VALVE = EnumProperty.of("valve", ValveState.class);
		public static final IntProperty EXTENSION = IntProperty.of("extension", 0, 4);

		// Ores
		public static final BooleanProperty PLACED = BooleanProperty.of("placed");

		// Conveyors
		public static final BooleanProperty MOVING = BooleanProperty.of("moving");
	}

	public enum ArmorMaterials implements ArmorMaterial {
		BRASS("brass", 13, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.ofItems(AssemblyItems.BRASS_INGOT)),
		EXOFRAME("exoframe", 10, new int[]{2, 4, 5, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.ofItems(AssemblyItems.BRASS_PLATE));

		private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
		private final String name;
		private final int durabilityMultiplier;
		private final int[] protectionAmounts;
		private final int enchantability;
		private final SoundEvent equipSound;
		private final float toughness;
		private final Lazy<Ingredient> repairIngredientSupplier;
		private final float knockbackResistance;

		ArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> ingredientSupplier) {
			this.name = name;
			this.durabilityMultiplier = durabilityMultiplier;
			this.protectionAmounts = protectionAmounts;
			this.enchantability = enchantability;
			this.equipSound = equipSound;
			this.toughness = toughness;
			this.repairIngredientSupplier = new Lazy<>(ingredientSupplier);
			this.knockbackResistance = knockbackResistance;
		}

		@Override
		public int getDurability(EquipmentSlot slot) {
			return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
		}

		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return this.protectionAmounts[slot.getEntitySlotId()];
		}

		@Override
		public int getEnchantability() {
			return this.enchantability;
		}

		@Override
		public SoundEvent getEquipSound() {
			return this.equipSound;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return this.repairIngredientSupplier.get();
		}

		@Override
		@Environment(EnvType.CLIENT)
		public String getName() {
			return this.name;
		}

		@Override
		public float getToughness() {
			return this.toughness;
		}

		@Override
		public float getKnockbackResistance() {
			return this.knockbackResistance;
		}
	}

	private static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
