package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.GrinderBlockEntity;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class AssemblyBlockEntities implements ModInitializer {
	public static BlockEntityType<WoodenBarrelBlockEntity> WOODEN_BARREL;
	public static BlockEntityType<TreeTapBlockEntity> TREE_TAP;
	public static BlockEntityType<GrinderBlockEntity> GRINDER;

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.Builder<T> builder) {
		BlockEntityType<T> blockEntityType = builder.method_11034(null);
		Registry.register(Registry.BLOCK_ENTITY, Assembly.MOD_ID + ":" + name, blockEntityType);
		return blockEntityType;
	}

	@Override
	public void onInitialize() {
		WOODEN_BARREL = register("wooden_barrel", BlockEntityType.Builder.create(WoodenBarrelBlockEntity::new));
		TREE_TAP = register("tree_tap", BlockEntityType.Builder.create(TreeTapBlockEntity::new));
		GRINDER = register("grinder", BlockEntityType.Builder.create(GrinderBlockEntity::new));
	}
}
