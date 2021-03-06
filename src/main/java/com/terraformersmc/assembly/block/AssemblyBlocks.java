package com.terraformersmc.assembly.block;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.fluid.AssemblyFluid;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.InteractionBypassBlockItem;
import com.terraformersmc.assembly.util.interaction.interactable.InteractionBypass;
import com.terraformersmc.assembly.world.AssemblyWorldgen;
import com.terraformersmc.terraform.block.*;
import com.terraformersmc.terraform.util.RecipeUtil;
import com.terraformersmc.terraform.util.TerraformSaplingGenerator;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SignItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssemblyBlocks {
	private static final Map<Identifier, BlockItem> ITEMS = new LinkedHashMap<>();
	private static final Map<Identifier, Block> BLOCKS = new LinkedHashMap<>();
	private static final Map<AssemblyFluid, AssemblyFluidBlock> FLUID_BLOCKS = new HashMap<>();

	public static final Block HEVEA_PLANKS = add("hevea_planks", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_SAPLING = add("hevea_sapling", new TerraformSaplingBlock(new TerraformSaplingGenerator(() -> AssemblyWorldgen.HEVEA_TREE_CONFIG)), ItemGroup.DECORATIONS);
	public static final Block POTTED_HEVEA_SAPLING = add("potted_hevea_sapling", new FlowerPotBlock(HEVEA_SAPLING, FabricBlockSettings.copyOf(Blocks.POTTED_OAK_SAPLING)));
	public static final Block STRIPPED_HEVEA_LOG = add("stripped_hevea_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).materialColor(MaterialColor.BROWN)), ItemGroup.BUILDING_BLOCKS);
	public static final Block STRIPPED_HEVEA_WOOD = add("stripped_hevea_wood", new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).materialColor(MaterialColor.BROWN)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_LOG = add("hevea_log", new HeveaLogBlock(() -> STRIPPED_HEVEA_LOG, MaterialColor.BROWN, FabricBlockSettings.copyOf(Blocks.DARK_OAK_LOG)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_WOOD = add("hevea_wood", new StrippableLogBlock(() -> STRIPPED_HEVEA_WOOD, MaterialColor.BROWN, FabricBlockSettings.copyOf(Blocks.DARK_OAK_LOG)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_LEAVES = add("hevea_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)), ItemGroup.DECORATIONS);
	public static final Block HEVEA_SLAB = add("hevea_slab", new SlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_PRESSURE_PLATE = add("hevea_pressure_plate", new TerraformPressurePlateBlock(FabricBlockSettings.copyOf(Blocks.OAK_PRESSURE_PLATE)), ItemGroup.REDSTONE);
	public static final Block HEVEA_FENCE = add("hevea_fence", new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE)), ItemGroup.DECORATIONS);
	public static final Block HEVEA_TRAPDOOR = add("hevea_trapdoor", new TerraformTrapdoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE)), ItemGroup.REDSTONE);
	public static final Block HEVEA_FENCE_GATE = add("hevea_fence_gate", new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE)), ItemGroup.REDSTONE);
	public static final Block HEVEA_STAIRS = add("hevea_stairs", new TerraformStairsBlock(HEVEA_PLANKS, FabricBlockSettings.copyOf(Blocks.OAK_STAIRS)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HEVEA_BUTTON = add("hevea_button", new TerraformButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_BUTTON)), ItemGroup.REDSTONE);
	public static final Block HEVEA_DOOR = add("hevea_door", new TerraformDoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_DOOR)), ItemGroup.REDSTONE);
	private static final Identifier HEVEA_SIGN_TEXTURE = new Identifier(Assembly.MOD_ID, "entity/sign/hevea_sign");
	public static final TerraformSignBlock HEVEA_SIGN = add("hevea_sign", new TerraformSignBlock(HEVEA_SIGN_TEXTURE, FabricBlockSettings.copyOf(Blocks.OAK_SIGN)));
	public static final Block HEVEA_WALL_SIGN = add("hevea_wall_sign", new TerraformWallSignBlock(HEVEA_SIGN_TEXTURE, FabricBlockSettings.copyOf(Blocks.OAK_WALL_SIGN)));
	public static final Item HEVEA_SIGN_ITEM = add("hevea_sign", new SignItem(new Item.Settings().maxCount(16).group(ItemGroup.DECORATIONS), HEVEA_SIGN, HEVEA_WALL_SIGN));

	public static final Block FLUID_BARREL = add("fluid_barrel", new FluidBarrelBlock(FabricBlockSettings.of(Material.WOOD).materialColor(MaterialColor.SPRUCE).hardness(2.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.DECORATIONS);
	public static final Block TREE_TAP = add("tree_tap", new TreeTapBlock(FabricBlockSettings.of(Material.WOOD).breakInstantly().sounds(BlockSoundGroup.WOOD).dropsNothing()), ItemGroup.DECORATIONS);

	public static final FluidBlock LATEX = add("latex", new AssemblyFluidBlock(AssemblyFluids.LATEX, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing()));
	//	public static final FluidBlock BIOMASS = add("biomass", new AssemblyFluidBlock(AssemblyFluids.BIOMASS, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing()));
	public static final FluidBlock CRUDE_OIL = add("crude_oil", new AssemblyFluidBlock(AssemblyFluids.CRUDE_OIL, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing()));
	public static final FluidBlock STEAM = add("steam", new AssemblyFluidBlock(AssemblyFluids.STEAM, FabricBlockSettings.of(Material.AIR).noCollision().hardness(100.0F).dropsNothing()));
	public static final FluidBlock FISH_OIL = add("fish_oil", new AssemblyFluidBlock(AssemblyFluids.FISH_OIL, FabricBlockSettings.of(Material.WATER).noCollision().hardness(100.0F).dropsNothing()));

	public static final Block BOILER = add("boiler", new BoilerBlock(FabricBlockSettings.of(Material.METAL).hardness(1.0F)));
	public static final Block BOILER_CHAMBER = add("boiler_chamber", new BoilerChamberBlock(FabricBlockSettings.of(Material.METAL).hardness(1.0F)), ItemGroup.DECORATIONS);
	public static final Block PRESS = add("press", new PressBlock(FabricBlockSettings.of(Material.METAL).dynamicBounds().hardness(1.0F)));
	public static final Block SQUEEZER = add("squeezer", new SqueezerBlock(FabricBlockSettings.of(Material.METAL).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 0)), ItemGroup.DECORATIONS);

	public static final Block COPPER_ORE = add("copper_ore", new AssemblyOreBlock(FabricBlockSettings.of(Material.STONE).strength(2.5F, 2.5F)), ItemGroup.BUILDING_BLOCKS);

	public static final Block COPPER_BLOCK = add("copper_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.BUILDING_BLOCKS);
	public static final Block ZINC_BLOCK = add("zinc_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.BUILDING_BLOCKS);
	public static final Block BRASS_BLOCK = add("brass_block", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.BUILDING_BLOCKS);

	public static final Block IRON_CONCENTRATE_BLOCK = add("iron_concentrate_block", new AssemblyFallingBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).breakByTool(FabricToolTags.SHOVELS)), ItemGroup.BUILDING_BLOCKS);
	public static final Block GOLD_CONCENTRATE_BLOCK = add("gold_concentrate_block", new AssemblyFallingBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).breakByTool(FabricToolTags.SHOVELS)), ItemGroup.BUILDING_BLOCKS);
	public static final Block COPPER_CONCENTRATE_BLOCK = add("copper_concentrate_block", new AssemblyFallingBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).breakByTool(FabricToolTags.SHOVELS)), ItemGroup.BUILDING_BLOCKS);
	public static final Block ZINC_CONCENTRATE_BLOCK = add("zinc_concentrate_block", new AssemblyFallingBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).breakByTool(FabricToolTags.SHOVELS)), ItemGroup.BUILDING_BLOCKS);
	public static final Block BRASS_BLEND_BLOCK = add("brass_blend_block", new AssemblyFallingBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).breakByTool(FabricToolTags.SHOVELS)), ItemGroup.BUILDING_BLOCKS);

	public static final Block CAPROCK = add("caprock", new Block(FabricBlockSettings.of(Material.STONE).hardness(4F).breakByTool(FabricToolTags.PICKAXES, 2)), ItemGroup.BUILDING_BLOCKS);
	public static final Block HALITE = add("halite", new Block(FabricBlockSettings.of(Material.STONE).hardness(1.5F).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.BUILDING_BLOCKS);

	public static final Block CONVEYOR_BELT = add("conveyor_belt", new ConveyorBeltBlock(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.REDSTONE);
	public static final Block FLUID_HOPPER = add("fluid_hopper", new FluidHopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.REDSTONE);
	public static final Block SPIGOT = add("spigot", new SpigotBlock(FabricBlockSettings.copyOf(Blocks.HOPPER).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.REDSTONE);
	public static final Block INJECTOR = add("injector", new InjectorBlock(FabricBlockSettings.copyOf(Blocks.HOPPER).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.DECORATIONS);
	public static final Block TINKERING_TABLE = add("tinkering_table", new TinkeringTableBlock(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).breakByTool(FabricToolTags.PICKAXES, 1)), ItemGroup.DECORATIONS);

	public static void register() {
		for (Identifier id : ITEMS.keySet()) {
			Registry.register(Registry.ITEM, id, ITEMS.get(id));
		}
		for (Identifier id : BLOCKS.keySet()) {
			Registry.register(Registry.BLOCK, id, BLOCKS.get(id));
		}

		FuelRegistry.INSTANCE.add(HEVEA_FENCE, 300);
		FuelRegistry.INSTANCE.add(HEVEA_FENCE_GATE, 300);

		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_PLANKS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_SLAB, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_FENCE_GATE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_FENCE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_STAIRS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(STRIPPED_HEVEA_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(STRIPPED_HEVEA_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(HEVEA_LEAVES, 30, 60);
	}

	private static <B extends Block> B add(String name, B block, ItemGroup tab) {
		Item.Settings settings = new Item.Settings();
		if (tab != null) {
			settings.group(tab);
		}
		if (block instanceof InteractionBypass) {
			return add(name, block, new InteractionBypassBlockItem((Block & InteractionBypass) block, settings));
		}
		return add(name, block, new BlockItem(block, settings));
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

	public static Map<Identifier, Block> getBlocks() {
		return BLOCKS;
	}

	public static Map<Identifier, BlockItem> getBlockItems() {
		return ITEMS;
	}
}
