package teamreborn.assembly.registry;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.blockentity.BoilerBlockEntity;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

import java.util.function.Supplier;

public class AssemblyBlockEntities {
	public static final BlockEntityType<WoodenBarrelBlockEntity> WOODEN_BARREL = add("wooden_barrel", WoodenBarrelBlockEntity::new, AssemblyBlocks.WOODEN_BARREL);
	public static final BlockEntityType<TreeTapBlockEntity> TREE_TAP = add("tree_tap", TreeTapBlockEntity::new, AssemblyBlocks.TREE_TAP);
	public static final BlockEntityType<BoilerBlockEntity> GRINDER = add("grinder", BoilerBlockEntity::new, AssemblyBlocks.GRINDER);

	public static <T extends BlockEntity> BlockEntityType<T> add(String name, Supplier<? extends T> supplier, Block... blocks) {
		return add(name, BlockEntityType.Builder.create(supplier, blocks));
	}

	public static <T extends BlockEntity> BlockEntityType<T> add(String name, BlockEntityType.Builder<T> builder) {
		return add(name, builder.build(null));
	}

	public static <T extends BlockEntity> BlockEntityType<T> add(String name, BlockEntityType<T> blockEntityType) {
		AssemblyRegistry.BLOCK_ENTITIES.put(new Identifier(Assembly.MOD_ID, name), blockEntityType);
		return blockEntityType;
	}

	public static void loadClass() {}
}
