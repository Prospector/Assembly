package teamreborn.assembly.registry;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.BoilerBlock;
import teamreborn.assembly.block.RubberLogBlock;
import teamreborn.assembly.block.TreeTapBlock;
import teamreborn.assembly.block.WoodenBarrelBlock;
import teamreborn.assembly.block.base.AssemblyFluidBlock;

public class AssemblyBlocks {
	public static final Block RUBBER_LOG = add("rubber_log", new RubberLogBlock(MaterialColor.WOOD, FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block WOODEN_BARREL = add("wooden_barrel", new WoodenBarrelBlock(FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block TREE_TAP = add("tree_tap", new TreeTapBlock(FabricBlockSettings.of(Material.WOOD).breakInstantly().sounds(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
	public static final Block GRINDER = add("grinder", new BoilerBlock(FabricBlockSettings.of(Material.METAL).build()), ItemGroup.DECORATIONS);
	public static final FluidBlock LATEX = addWithoutItem("latex", new AssemblyFluidBlock(AssemblyFluids.LATEX, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing().build()));
	public static final FluidBlock BIOMASS = addWithoutItem("biomass", new AssemblyFluidBlock(AssemblyFluids.BIOMASS, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing().build()));

	public static <B extends Block> B add(String name, B block, ItemGroup tab) {
		return add(name, block, new BlockItem(block, new Item.Settings().itemGroup(tab)));
	}

	public static <B extends Block> B add(String name, B block, BlockItem item) {
		AssemblyRegistry.BLOCKS.put(new Identifier(Assembly.MOD_ID, name), block);
		if (item != null) {
			item.registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
			AssemblyItems.add(name, item);
		}
		return block;
	}

	public static <B extends Block> B addWithoutItem(String name, B block) {
		AssemblyRegistry.BLOCKS.put(new Identifier(Assembly.MOD_ID, name), block);
		return block;
	}

	public static void loadClass() {}
}
