package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sortme.ItemGroup;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;

public class AssemblyItems implements ModInitializer {

	public static final Item SAP_BUCKET;
	public static final Item BIOMASS_BUCKET;

	static {
		SAP_BUCKET = register("sap_bucket", new BucketItem(AssemblyFluids.SAP, new Item.Builder().containerItem(Items.BUCKET).stackSize(1).itemGroup(ItemGroup.MISC)));
		BIOMASS_BUCKET = register("biomass_bucket", new BucketItem(AssemblyFluids.BIOMASS, new Item.Builder().containerItem(Items.BUCKET).stackSize(1).itemGroup(ItemGroup.MISC)));
	}

	public static Item register(String name, Item item) {
		Registry.register(Registry.ITEMS, Assembly.MOD_ID + ":" + name, item);
		return item;
	}

	@Override
	public void onInitialize() { }
}
