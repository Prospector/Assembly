package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapeContainer;
import net.minecraft.world.BlockView;
import prospector.silk.block.SilkBlockWithEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class WoodenBarrelBlock extends SilkBlockWithEntity {
	protected static final VoxelShapeContainer BOUNDING_SHAPE = Block.createCubeShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public WoodenBarrelBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShapeContainer getBoundingShape(BlockState state, BlockView world, BlockPos pos) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new WoodenBarrelBlockEntity();
	}
}
