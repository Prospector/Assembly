package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.block.BlockItem;
import net.minecraft.sortme.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.MapColor;
import net.minecraft.util.registry.Registry;
import prospector.silk.util.SilkBlockBuilder;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.RubberLogBlock;
import teamreborn.assembly.block.SappingBarrelBlock;
import teamreborn.assembly.block.TreeTapBlock;

public class AssemblyBlocks implements ModInitializer {
	public static final Block RUBBER_LOG;
	public static final Block SAPPING_BARREL;
	public static final Block TREE_TAP;

	static {
		RUBBER_LOG = AssemblyBlocks.register("rubber_log", new RubberLogBlock(MapColor.WOOD, SilkBlockBuilder.create(Material.WOOD).setStrength(2.0F).setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
		SAPPING_BARREL = AssemblyBlocks.register("sapping_barrel", new SappingBarrelBlock(SilkBlockBuilder.create(Material.WOOD).setStrength(2.0F).setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
		TREE_TAP = AssemblyBlocks.register("tree_tap", new TreeTapBlock(SilkBlockBuilder.create(Material.WOOD).setStrength(2.0F).setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
	}

	public static Block register(String name, Block block, ItemGroup tab) {
		Registry.register(Registry.BLOCKS, Assembly.MOD_ID + ":" + name, block);
		BlockItem item = new BlockItem(block, new Item.Builder().itemGroup(tab));
		item.registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
		AssemblyItems.register(name, item);
		return block;
	}

	@Override
	public void onInitialize() { }
}
