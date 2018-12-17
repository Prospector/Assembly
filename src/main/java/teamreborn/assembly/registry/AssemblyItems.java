package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;

public class AssemblyItems implements ModInitializer {

	public static Item LATEX_BUCKET;
	public static Item BIOMASS_BUCKET;

	public static Item register(String name, Item item) {
		Registry.register(Registry.ITEM, Assembly.MOD_ID + ":" + name, item);
		return item;
	}

	@Override
	public void onInitialize() {
		LATEX_BUCKET = register("latex_bucket", new BucketItem(AssemblyFluids.LATEX, new Item.Settings().recipeRemainder(Items.BUCKET).stackSize(1).itemGroup(ItemGroup.MISC)));
		BIOMASS_BUCKET = register("biomass_bucket", new BucketItem(AssemblyFluids.BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).stackSize(1).itemGroup(ItemGroup.MISC)));
	}
}
