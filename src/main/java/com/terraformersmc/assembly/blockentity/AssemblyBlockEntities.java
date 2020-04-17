package com.terraformersmc.assembly.blockentity;

import com.terraformersmc.assembly.Assembly;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.terraformersmc.assembly.block.AssemblyBlocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AssemblyBlockEntities {
	private static final Map<Identifier, BlockEntityType<? extends BlockEntity>> BLOCK_ENTITY_TYPES = new HashMap<>();

	public static final BlockEntityType<FluidBarrelBlockEntity> FLUID_BARREL = add("wooden_barrel", FluidBarrelBlockEntity::new, AssemblyBlocks.FLUID_BARREL);
	public static final BlockEntityType<TreeTapBlockEntity> TREE_TAP = add("tree_tap", TreeTapBlockEntity::new, AssemblyBlocks.TREE_TAP);
	public static final BlockEntityType<SteamPressBlockEntity> STEAM_PRESS = add("steam_press", SteamPressBlockEntity::new, AssemblyBlocks.STEAM_PRESS);
	public static final BlockEntityType<BoilerBlockEntity> BOILER = add("boiler", BoilerBlockEntity::new, AssemblyBlocks.BOILER);
	public static final BlockEntityType<BoilerChamberBlockEntity> BOILER_CHAMBER = add("boiler_chamber", BoilerChamberBlockEntity::new, AssemblyBlocks.BOILER_CHAMBER);
	public static final BlockEntityType<FluidHopperBlockEntity> FLUID_HOPPER = add("fluid_hopper", FluidHopperBlockEntity::new, AssemblyBlocks.FLUID_HOPPER);
	public static final BlockEntityType<SpigotBlockEntity> SPIGOT = add("spigot", SpigotBlockEntity::new, AssemblyBlocks.SPIGOT);

	private static <T extends BlockEntity> BlockEntityType<T> add(String name, Supplier<? extends T> supplier, Block... blocks) {
		return add(name, BlockEntityType.Builder.create(supplier, blocks));
	}

	private static <T extends BlockEntity> BlockEntityType<T> add(String name, BlockEntityType.Builder<T> builder) {
		return add(name, builder.build(null));
	}

	private static <T extends BlockEntity> BlockEntityType<T> add(String name, BlockEntityType<T> blockEntityType) {
		BLOCK_ENTITY_TYPES.put(new Identifier(Assembly.MOD_ID, name), blockEntityType);
		return blockEntityType;
	}

	public static void register() {
		for (Identifier id : BLOCK_ENTITY_TYPES.keySet()) {
			Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BLOCK_ENTITY_TYPES.get(id));
		}
	}

	public static Map<Identifier, BlockEntityType<? extends BlockEntity>> getBlockEntityTypes() {
		return BLOCK_ENTITY_TYPES;
	}
}
