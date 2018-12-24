package teamreborn.assembly.registry;

import net.fabricmc.fabric.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.GrinderBlock;
import teamreborn.assembly.block.RubberLogBlock;
import teamreborn.assembly.block.TreeTapBlock;
import teamreborn.assembly.block.WoodenBarrelBlock;

public class AssemblyBlocks {
	public static final Block RUBBER_LOG = add("rubber_log", new RubberLogBlock(MaterialColor.WOOD, FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block WOODEN_BARREL = add("wooden_barrel", new WoodenBarrelBlock(FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block TREE_TAP = add("tree_tap", new TreeTapBlock(FabricBlockSettings.create(Material.WOOD).breakInstantly().setSoundGroup(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
	public static final Block GRINDER = add("grinder", new GrinderBlock(FabricBlockSettings.create(Material.METAL).build()), ItemGroup.DECORATIONS);

	public static Block add(String name, Block block, ItemGroup tab) {
		return add(name, block, new BlockItem(block, new Item.Settings().itemGroup(tab)));
	}

	public static Block add(String name, Block block, BlockItem item) {
		AssemblyRegistry.BLOCKS.put(new Identifier(Assembly.MOD_ID, name), block);
		if (item != null) {
			item.registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
			AssemblyItems.add(name, item);
		}
		return block;
	}

	public static void loadClass() {}
}
