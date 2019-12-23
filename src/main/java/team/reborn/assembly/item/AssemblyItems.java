package team.reborn.assembly.item;

import net.minecraft.item.BedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.fluid.AssemblyFluid;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.Assembly;

import java.util.HashMap;
import java.util.Map;

public class AssemblyItems {

	private static final Map<Identifier, Item> ITEMS = new HashMap<>();
	private static final Map<AssemblyFluid, AssemblyBucketItem> BUCKETS = new HashMap<>();

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

	public static final Item STEAM_PRESS = add("steam_press", new BedItem(AssemblyBlocks.STEAM_PRESS, new Item.Settings().group(ItemGroup.DECORATIONS)));

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
