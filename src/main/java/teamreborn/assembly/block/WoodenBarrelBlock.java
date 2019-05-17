package teamreborn.assembly.block;

import io.github.prospector.silk.block.SilkBlockWithEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class WoodenBarrelBlock extends SilkBlockWithEntity {
	protected static final VoxelShape BOUNDING_SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public WoodenBarrelBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new WoodenBarrelBlockEntity();
	}
}
