package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.SappingBarrelBlockEntity;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;

public class AssemblyBlockEntities implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final BlockEntityType<SappingBarrelBlockEntity> SAPPING_BARREL_BLOCK_ENTITY;
	public static final BlockEntityType<TreeTapBlockEntity> TREE_TAP_BLOCK_ENTITY;

	static {
		SAPPING_BARREL_BLOCK_ENTITY = register("sapping_barrel", BlockEntityType.Builder.create(SappingBarrelBlockEntity::new));
		TREE_TAP_BLOCK_ENTITY = register("tree_tap", BlockEntityType.Builder.create(TreeTapBlockEntity::new));
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
