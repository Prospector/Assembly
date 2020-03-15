package team.reborn.assembly.util;

import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.RawFluidTagFilter;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import team.reborn.assembly.block.enums.ValveState;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.tags.AssemblyFluidTags;

import java.util.Set;
import java.util.function.Supplier;

public class AssemblyConstants {
	public static final String MOD_ID = "assembly";
	public static final String COMMON_NAMESPACE = "c";
	public static final Set<String> AUTHORS = ImmutableSet.of("9927b75a-89d9-4ff7-8bac-58aaed08911a");

	public static class NbtKeys {
		public static final String FLUIDS = "Fluids";
		public static final String INPUT_FLUIDS = "InputFluids";
		public static final String OUTPUT_FLUIDS = "OutputFluids";

		public static final String RECIPE = "Recipe";

		//Fluid Hopper & Spigot
		public static final String TRANSFER_COOLDOWN = "TransferCooldown";
		public static final String CUSTOM_NAME = "CustomName";

		//Spigot
		public static final String POURING_FLUID = "PouringFluid";

		//Steam Press
		public static final String BURN_TIME = "BurnTime";
		public static final String PRESS_PROGRESS = "Progress";
		public static final String PRESS_RESET = "Reset";
		public static final String CURRENT_PRESSES = "CurrentPresses";
		public static final String MASTER = "Master";

		//Piston Boots
		public static final String EXTENDED = "Extended";
	}

	public static class Ids {
		public static final Identifier BOILER = id("boiler");

		public static final Identifier AUTHOR_CAPE = id("textures/entity/player/author_cape.png");
		public static final Identifier CONTRIBUTOR_CAPE = id("textures/entity/player/contributor_cape.png");

		public static final Identifier STEAM_TANK = id("steam_tank");
		public static final Identifier EXOFRAME = id("exoframe");
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

	public static class FluidFilters {
		public static final FluidFilter STEAM = new RawFluidTagFilter(AssemblyFluidTags.STEAM);
	}

	public enum ArmorMaterials implements ArmorMaterial {
		BRASS("brass", 13, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.ofItems(AssemblyItems.BRASS_INGOT)),
		EXOFRAME("exoframe", 10, new int[]{2, 4, 5, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.ofItems(AssemblyItems.BRASS_PLATE));

		private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
		private final String name;
		private final int durabilityMultiplier;
		private final int[] protectionAmounts;
		private final int enchantability;
		private final SoundEvent equipSound;
		private final float toughness;
		private final Lazy<Ingredient> repairIngredientSupplier;

		ArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, Supplier<Ingredient> ingredientSupplier) {
			this.name = name;
			this.durabilityMultiplier = durabilityMultiplier;
			this.protectionAmounts = protectionAmounts;
			this.enchantability = enchantability;
			this.equipSound = equipSound;
			this.toughness = toughness;
			this.repairIngredientSupplier = new Lazy<>(ingredientSupplier);
		}

		public int getDurability(EquipmentSlot slot) {
			return BASE_DURABILITY[slot.getEntitySlotId()] * this.durabilityMultiplier;
		}

		public int getProtectionAmount(EquipmentSlot slot) {
			return this.protectionAmounts[slot.getEntitySlotId()];
		}

		public int getEnchantability() {
			return this.enchantability;
		}

		public SoundEvent getEquipSound() {
			return this.equipSound;
		}

		public Ingredient getRepairIngredient() {
			return this.repairIngredientSupplier.get();
		}

		@Environment(EnvType.CLIENT)
		public String getName() {
			return this.name;
		}

		public float getToughness() {
			return this.toughness;
		}
	}

	private static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
