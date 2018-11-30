package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapeContainer;
import net.minecraft.world.BlockView;
import teamreborn.assembly.blockentity.SappingBarrelBlockEntity;
import prospector.silk.block.SilkBlockWithEntity;

public class SappingBarrelBlock extends SilkBlockWithEntity {
	protected static final VoxelShapeContainer BOUNDING_SHAPE = Block.createCubeShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public SappingBarrelBlock(Builder builder) {
		super(builder);
	}

	public VoxelShapeContainer getBoundingShape(BlockState var1, BlockView var2, BlockPos var3) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new SappingBarrelBlockEntity();
	}
}
