package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.GrinderBlock;
import teamreborn.assembly.block.RubberLogBlock;
import teamreborn.assembly.block.TreeTapBlock;
import teamreborn.assembly.block.WoodenBarrelBlock;

public class AssemblyBlocks implements ModInitializer {
	public static Block RUBBER_LOG;
	public static Block WOODEN_BARREL;
	public static Block TREE_TAP;
	public static Block GRINDER;

	public static Block register(String name, Block block, ItemGroup tab) {
		Registry.register(Registry.BLOCK, Assembly.MOD_ID + ":" + name, block);
		BlockItem item = new BlockItem(block, new Item.Settings().itemGroup(tab));
		item.registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
		AssemblyItems.register(name, item);
		return block;
	}

	@Override
	public void onInitialize() {
		RUBBER_LOG = AssemblyBlocks.register("rubber_log", new RubberLogBlock(MaterialColor.WOOD, FabricBlockSettings.create(Material.WOOD).setStrength(2.0F, 2.0F).setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
		WOODEN_BARREL = AssemblyBlocks.register("wooden_barrel", new WoodenBarrelBlock(FabricBlockSettings.create(Material.WOOD).setStrength(2.0F, 2.0F).setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
		TREE_TAP = AssemblyBlocks.register("tree_tap", new TreeTapBlock(FabricBlockSettings.create(Material.WOOD).setStrength(2.0F, 2.0F).setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
		GRINDER = AssemblyBlocks.register("grinder", new GrinderBlock(FabricBlockSettings.create(Material.STONE).build()), ItemGroup.MISC);
	}
}
