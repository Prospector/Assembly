package teamreborn.assembly.block;

import com.terraformersmc.terraform.block.*;
import com.terraformersmc.terraform.util.RecipeUtil;
import com.terraformersmc.terraform.util.TerraformSaplingGenerator;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.base.AssemblyFluidBlock;
import teamreborn.assembly.fluid.AssemblyFluid;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.world.AssemblyWorldgen;

import java.util.HashMap;
import java.util.Map;

public class AssemblyBlocks {
	private static final Map<Identifier, BlockItem> ITEMS = new HashMap<>();
	private static final Map<Identifier, Block> BLOCKS = new HashMap<>();
	private static final Map<AssemblyFluid, AssemblyFluidBlock> FLUID_BLOCKS = new HashMap<>();

	public static final Block HEVEA_PLANKS = add("hevea_planks", new Block(FabricBlockSettings.copy(Blocks.OAK_PLANKS).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_SAPLING = add("hevea_sapling", new TerraformSaplingBlock(new TerraformSaplingGenerator(() -> new OakTreeFeature(BranchedTreeFeatureConfig::deserialize2), () -> AssemblyWorldgen.HEVEA_TREE_CONFIG)), ItemGroup.DECORATIONS);
	public static final Block POTTED_HEVEA_SAPLING = add("potted_hevea_sapling", new FlowerPotBlock(HEVEA_SAPLING, FabricBlockSettings.copy(Blocks.POTTED_OAK_SAPLING).build()));
	public static final Block STRIPPED_HEVEA_LOG = add("stripped_hevea_log", new LogBlock(MaterialColor.BROWN, FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block STRIPPED_HEVEA_WOOD = add("stripped_hevea_wood", new LogBlock(MaterialColor.BROWN, FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_LOG = add("hevea_log", new HeveaLogBlock(() -> STRIPPED_HEVEA_LOG, MaterialColor.BROWN, FabricBlockSettings.copy(Blocks.DARK_OAK_LOG).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_WOOD = add("hevea_wood", new StrippableLogBlock(() -> STRIPPED_HEVEA_WOOD, MaterialColor.BROWN, FabricBlockSettings.copy(Blocks.DARK_OAK_LOG).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_LEAVES = add("hevea_leaves", new LeavesBlock(FabricBlockSettings.copy(Blocks.OAK_LEAVES).build()), ItemGroup.DECORATIONS);
	public static final Block HEVEA_SLAB = add("hevea_slab", new SlabBlock(FabricBlockSettings.copy(Blocks.OAK_SLAB).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_PRESSURE_PLATE = add("hevea_pressure_plate", new TerraformPressurePlateBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE).build()), ItemGroup.REDSTONE);
	public static final Block HEVEA_FENCE = add("hevea_fence", new FenceBlock(FabricBlockSettings.copy(Blocks.OAK_FENCE).build()), ItemGroup.DECORATIONS);
	public static final Block HEVEA_TRAPDOOR = add("hevea_trapdoor", new TerraformTrapdoorBlock(FabricBlockSettings.copy(Blocks.OAK_FENCE).build()), ItemGroup.REDSTONE);
	public static final Block HEVEA_FENCE_GATE = add("hevea_fence_gate", new FenceGateBlock(FabricBlockSettings.copy(Blocks.OAK_FENCE).build()), ItemGroup.REDSTONE);
	public static final Block HEVEA_STAIRS = add("hevea_stairs", new TerraformStairsBlock(HEVEA_PLANKS, FabricBlockSettings.copy(Blocks.OAK_STAIRS).build()), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_BUTTON = add("hevea_button", new TerraformButtonBlock(FabricBlockSettings.copy(Blocks.OAK_BUTTON).build()), ItemGroup.REDSTONE);
	public static final Block HEVEA_DOOR = add("hevea_door", new TerraformDoorBlock(FabricBlockSettings.copy(Blocks.OAK_DOOR).build()), ItemGroup.REDSTONE);
	private static final Identifier HEVEA_SIGN_TEXTURE = new Identifier(Assembly.MOD_ID, "textures/entity/sign/hevea.png");
	public static final TerraformSignBlock HEVEA_SIGN = add("hevea_sign", new TerraformSignBlock(HEVEA_SIGN_TEXTURE, FabricBlockSettings.copy(Blocks.OAK_SIGN).build()));
	public static final Block HEVEA_WALL_SIGN = add("hevea_wall_sign", new TerraformWallSignBlock(HEVEA_SIGN_TEXTURE, FabricBlockSettings.copy(Blocks.OAK_WALL_SIGN).build()));
	public static final Item HEVEA_SIGN_ITEM = add("hevea_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), HEVEA_SIGN, HEVEA_WALL_SIGN));

	public static final Block WOODEN_BARREL = add("wooden_barrel", new WoodenBarrelBlock(FabricBlockSettings.copy(Blocks.OAK_LOG).build()), ItemGroup.DECORATIONS);
	public static final Block TREE_TAP = add("tree_tap", new TreeTapBlock(FabricBlockSettings.of(Material.WOOD).breakInstantly().sounds(BlockSoundGroup.WOOD).build()), ItemGroup.DECORATIONS);
//	public static final Block TUBE = add("tube", new TubeBlock(FabricBlockSettings.of(Material.METAL).breakInstantly().sounds(BlockSoundGroup.METAL).build()), ItemGroup.DECORATIONS);

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
			RecipeUtil.registerCompostableBlock(block);
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

	private static <I extends BlockItem> I add(String name, I item) {
		item.appendBlocks(Item.BLOCK_ITEMS, item);
		ITEMS.put(new Identifier(Assembly.MOD_ID, name), item);
		return item;
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
