package teamreborn.assembly.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.AssemblyFluid;
import teamreborn.assembly.fluid.AssemblyFluids;

import java.util.HashMap;
import java.util.Map;

public class AssemblyItems {

	private static final Map<Identifier, Item> ITEMS = new HashMap<>();
	private static final Map<AssemblyFluid, AssemblyBucketItem> BUCKETS = new HashMap<>();

	public static Item SALT = add("salt", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static Item SULFUR = add("sulfur", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static Item CRUDE_RUBBER = add("crude_rubber", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static Item VULCANIZED_RUBBER = add("vulcanized_rubber", new Item(new Item.Settings().group(ItemGroup.MATERIALS)));
	public static Item LATEX_BUCKET = add("latex_bucket", new AssemblyBucketItem(AssemblyFluids.LATEX, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	public static Item BIOMASS_BUCKET = add("biomass_bucket", new AssemblyBucketItem(AssemblyFluids.BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));
	public static Item OIL_BUCKET = add("oil_bucket", new AssemblyBucketItem(AssemblyFluids.OIL, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC)));

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
