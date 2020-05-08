package com.terraformersmc.assembly.item;

import com.terraformersmc.assembly.Assembly;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.fluid.AssemblyFluid;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.exoframe.ExoframePieceItem;
import com.terraformersmc.assembly.item.exoframe.PistonBootsItem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssemblyItems {

	private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
	private static final Map<AssemblyFluid, AssemblyBucketItem> BUCKETS = new HashMap<>();

	public static final Item IRON_CONCENTRATE = add("iron_concentrate", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item GOLD_CONCENTRATE = add("gold_concentrate", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item COPPER_CONCENTRATE = add("copper_concentrate", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item ZINC_CONCENTRATE = add("zinc_concentrate", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BRASS_BLEND = add("brass_blend", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item COPPER_INGOT = add("copper_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item ZINC_INGOT = add("zinc_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BRASS_INGOT = add("brass_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BRASS_GEAR = add("brass_gear", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BRASS_PLATE = add("brass_plate", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item SALT = add("salt", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item SULFUR = add("sulfur", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item COAGULATED_LATEX = add("coagulated_latex", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CRUDE_RUBBER = add("crude_rubber", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item VULCANIZED_RUBBER = add("vulcanized_rubber", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item VENOMOUS_FANG = add("venomous_fang", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item FORMIC_ACID = add("formic_acid", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));

	public static final Item LATEX_BUCKET = add("latex_bucket", new AssemblyBucketItem(AssemblyFluids.LATEX, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	//	public static final Item BIOMASS_BUCKET = add("biomass_bucket", new AssemblyBucketItem(AssemblyFluids.BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	public static final Item CRUDE_OIL_BUCKET = add("crude_oil_bucket", new AssemblyBucketItem(AssemblyFluids.CRUDE_OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	public static final Item STEAM_BUCKET = add("steam_bucket", new AssemblyBucketItem(AssemblyFluids.STEAM, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));

	public static final Item STEAM_PRESS = add("steam_press", new BedItem(AssemblyBlocks.STEAM_PRESS, new Item.Settings().group(ItemGroup.DECORATIONS)));

	public static final Item DIPSTICK = add("dipstick", new DipstickItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1)));

	public static final Item WOODEN_HAMMER = add("wooden_hammer", new HammerItem(ToolMaterials.WOOD, 3.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STONE_HAMMER = add("stone_hammer", new HammerItem(ToolMaterials.STONE, 3.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item IRON_HAMMER = add("iron_hammer", new HammerItem(ToolMaterials.IRON, 3.0F, -3.1F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item GOLDEN_HAMMER = add("golden_hammer", new HammerItem(ToolMaterials.GOLD, 3.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item DIAMOND_HAMMER = add("diamond_hammer", new HammerItem(ToolMaterials.DIAMOND, 3.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item NETHERITE_HAMMER = add("netherite_hammer", new HammerItem(ToolMaterials.NETHERITE, 3.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));

	public static final Item EXOFRAME_HEADPIECE = add("exoframe_headpiece", new ExoframePieceItem(EquipmentSlot.HEAD, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item EXOFRAME_CHESTPIECE = add("exoframe_chestpiece", new ExoframePieceItem(EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item EXOFRAME_LEGPIECE = add("exoframe_legpiece", new ExoframePieceItem(EquipmentSlot.LEGS, new Item.Settings().group(ItemGroup.COMBAT)));
	public static final Item EXOFRAME_FOOTPIECE = add("exoframe_footpiece", new ExoframePieceItem(EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT)));

	public static final Item PISTON_BOOTS = add("piston_boots", new PistonBootsItem(new Item.Settings().group(ItemGroup.COMBAT)));

	private static Item add(String name, Item item) {
		ITEMS.put(new Identifier(Assembly.MOD_ID, name), item);
		if (item instanceof AssemblyBucketItem) {
			BUCKETS.put(((AssemblyBucketItem) item).getFluid(), (AssemblyBucketItem) item);
			BUCKETS.put(AssemblyFluids.getInverse(((AssemblyBucketItem) item).getFluid()), (AssemblyBucketItem) item);
		}
		return item;
	}

	public static AssemblyBucketItem getBucket(AssemblyFluid fluid) {
		return BUCKETS.get(fluid);
	}

	public static void register() {
		for (Identifier id : ITEMS.keySet()) {
			Registry.register(Registry.ITEM, id, ITEMS.get(id));
		}
	}

	public static Map<Identifier, Item> getItems() {
		return ITEMS;
	}
}
