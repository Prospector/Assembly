package teamreborn.assembly.block;

import io.github.prospector.silk.block.SilkLeavesBlock;
import io.github.prospector.silk.block.SilkSaplingBlock;
import io.github.prospector.silk.util.SilkSaplingGenerator;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.base.AssemblyFluidBlock;
import teamreborn.assembly.fluid.AssemblyFluid;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.world.feature.RubberTreeFeature;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.HashMap;
import java.util.Map;

public class AssemblyBlocks {
	private static final Map<Identifier, BlockItem> ITEMS = new HashMap<>();
	private static final Map<Identifier, Block> BLOCKS = new HashMap<>();
	private static final Map<AssemblyFluid, AssemblyFluidBlock> FLUID_BLOCKS = new HashMap<>();

	public static final Block RUBBER_LOG = add("rubber_log", new RubberLogBlock(MaterialColor.WOOD, FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block RUBBER_LEAVES = add("rubber_leaves", new SilkLeavesBlock(), ItemGroup.DECORATIONS);
	public static final Block RUBBER_SAPLING = add("rubber_sapling", new SilkSaplingBlock(new SilkSaplingGenerator(() -> new RubberTreeFeature(DefaultFeatureConfig::deserialize, true))), ItemGroup.DECORATIONS);
	public static final Block POTTED_RUBBER_SAPLING = add("potted_rubber_sapling", new FlowerPotBlock(RUBBER_SAPLING, FabricBlockSettings.copy(Blocks.POTTED_OAK_SAPLING).build()));
	public static final Block WOODEN_BARREL = add("wooden_barrel", new WoodenBarrelBlock(FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block TREE_TAP = add("tree_tap", new TreeTapBlock(FabricBlockSettings.of(Material.WOOD).breakInstantly().sounds(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
	public static final FluidBlock LATEX = add("latex", new AssemblyFluidBlock(AssemblyFluids.LATEX, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing().build()));
	public static final FluidBlock BIOMASS = add("biomass", new AssemblyFluidBlock(AssemblyFluids.BIOMASS, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing().build()));
	public static final FluidBlock OIL = add("oil", new AssemblyFluidBlock(AssemblyFluids.OIL, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing().build()));

	private static <B extends Block> B add(String name, B block, ItemGroup tab) {
		return add(name, block, new BlockItem(block, new Item.Settings().group(tab)));
	}

	private static <B extends Block> B add(String name, B block, BlockItem item) {
		add(name, block);
		if (item != null) {
			item.appendBlocks(Item.BLOCK_ITEMS, item);
			ITEMS.put(new Identifier(Assembly.MOD_ID, name), item);
		}
		return block;
	}

	private static <B extends Block> B add(String name, B block) {
		BLOCKS.put(new Identifier(Assembly.MOD_ID, name), block);
		if (block instanceof AssemblyFluidBlock) {
			FLUID_BLOCKS.put(((AssemblyFluidBlock) block).getFluid(), (AssemblyFluidBlock) block);
			FLUID_BLOCKS.put(AssemblyFluids.getInverse(((AssemblyFluidBlock) block).getFluid()), (AssemblyFluidBlock) block);
		}
		return block;
	}

	public static AssemblyFluidBlock getFluidBlock(AssemblyFluid fluid) {
		return FLUID_BLOCKS.get(fluid);
	}

	public static void register() {
		for (Identifier id : ITEMS.keySet()) {
			Registry.register(Registry.ITEM, id, ITEMS.get(id));
		}
		for (Identifier id : BLOCKS.keySet()) {
			Registry.register(Registry.BLOCK, id, BLOCKS.get(id));
		}
	}

	public static Map<Identifier, Block> getBlocks() {
		return BLOCKS;
	}

	public static Map<Identifier, BlockItem> getBlockItems() {
		return ITEMS;
	}
}
