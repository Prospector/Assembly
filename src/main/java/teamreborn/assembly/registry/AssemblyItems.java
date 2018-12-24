package teamreborn.assembly.registry;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;

public class AssemblyItems {

	public static Item LATEX_BUCKET = add("latex_bucket", new BucketItem(AssemblyFluids.LATEX, new Item.Settings().recipeRemainder(Items.BUCKET).stackSize(1).itemGroup(ItemGroup.MISC)));
	public static Item BIOMASS_BUCKET = add("biomass_bucket", new BucketItem(AssemblyFluids.BIOMASS, new Item.Settings().recipeRemainder(Items.BUCKET).stackSize(1).itemGroup(ItemGroup.MISC)));

	public static Item add(String name, Item item) {
		AssemblyRegistry.ITEMS.put(new Identifier(Assembly.MOD_ID, name), item);
		return item;
	}

	public static void loadClass() {}
}
