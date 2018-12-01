package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class AssemblyBlockEntities implements ModInitializer {
	public static final BlockEntityType<WoodenBarrelBlockEntity> WOODEN_BARREL;
	public static final BlockEntityType<TreeTapBlockEntity> TREE_TAP;

	static {
		WOODEN_BARREL = register("wooden_barrel", BlockEntityType.Builder.create(WoodenBarrelBlockEntity::new));
		TREE_TAP = register("tree_tap", BlockEntityType.Builder.create(TreeTapBlockEntity::new));
	}

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.Builder<T> builder) {
		BlockEntityType<T> blockEntityType = builder.method_11034(null);
		Registry.register(Registry.BLOCK_ENTITIES, Assembly.MOD_ID + ":" + name, blockEntityType);
		return blockEntityType;
	}

	@Override
	public void onInitialize() {
	}
}
