package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;

public class AssemblyBlockEntities implements ModInitializer {
	public static final BlockEntityType<BarrelBlockEntity> SAPPING_BARREL_BLOCK_ENTITY;
	public static final BlockEntityType<TreeTapBlockEntity> TREE_TAP_BLOCK_ENTITY;

	static {
		SAPPING_BARREL_BLOCK_ENTITY = register("sapping_barrel", BlockEntityType.Builder.create(BarrelBlockEntity::new));
		TREE_TAP_BLOCK_ENTITY = register("tree_tap", BlockEntityType.Builder.create(TreeTapBlockEntity::new));
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.Builder<T> builder) {
		BlockEntityType<T> blockEntityType;
		Registry.register(Registry.BLOCK_ENTITIES, Assembly.MOD_ID + ":" + name, blockEntityType = builder.method_11034(null));
		return blockEntityType;
	}

	@Override
	public void onInitialize() { }
}
