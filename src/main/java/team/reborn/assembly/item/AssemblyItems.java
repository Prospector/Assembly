package team.reborn.assembly.item;

import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.fluid.AssemblyFluid;
import team.reborn.assembly.fluid.AssemblyFluids;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssemblyItems {

	private static final Map<Identifier, Item> ITEMS = new LinkedHashMap<>();
	private static final Map<AssemblyFluid, AssemblyBucketItem> BUCKETS = new HashMap<>();

	public static final Item COPPER_INGOT = add("copper_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item ZINC_INGOT = add("zinc_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item BRASS_INGOT = add("brass_ingot", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item SALT = add("salt", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item SULFUR = add("sulfur", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item COAGULATED_LATEX = add("coagulated_latex", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item CRUDE_RUBBER = add("crude_rubber", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item VULCANIZED_RUBBER = add("vulcanized_rubber", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item VENOMOUS_FANG = add("venomous_fang", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static final Item FORMIC_ACID = add("formic_acid", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));

	public static final Item LATEX_BUCKET = add("latex_bucket", new AssemblyBucketItem(AssemblyFluids.LATEX, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	//	public static final  Item BIOMASS_BUCKET = add("biomass_bucket", new AssemblyBucketItem(AssemblyFluids.BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	public static final Item OIL_BUCKET = add("oil_bucket", new AssemblyBucketItem(AssemblyFluids.OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	public static final Item STEAM_BUCKET = add("steam_bucket", new AssemblyBucketItem(AssemblyFluids.STEAM, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));

	public static final Item STEAM_PRESS = add("steam_press", new BedItem(AssemblyBlocks.STEAM_PRESS, new Item.Settings().group(ItemGroup.DECORATIONS)));

	public static final Item DIPSTICK = add("dipstick", new DipstickItem(new Item.Settings().group(ItemGroup.TOOLS)));

	public static final Item WOODEN_HAMMER = add("wooden_hammer", new HammerItem(ToolMaterials.WOOD, 3.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item STONE_HAMMER = add("stone_hammer", new HammerItem(ToolMaterials.STONE, 3.0F, -3.2F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item IRON_HAMMER = add("iron_hammer", new HammerItem(ToolMaterials.IRON, 3.0F, -3.1F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item GOLDEN_HAMMER = add("golden_hammer", new HammerItem(ToolMaterials.GOLD, 3.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));
	public static final Item DIAMOND_HAMMER = add("diamond_hammer", new HammerItem(ToolMaterials.DIAMOND, 3.0F, -3.0F, new Item.Settings().group(ItemGroup.TOOLS)));


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
