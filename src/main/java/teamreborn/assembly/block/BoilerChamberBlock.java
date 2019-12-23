package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BoilerChamberBlock extends Block {

	public static final VoxelShape SHAPE;

	public BoilerChamberBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return SHAPE;
	}

	static {
		VoxelShape bottom = Block.createCuboidShape(2, 0, 2, 14, 3, 14);
		VoxelShape middle = Block.createCuboidShape(3, 3, 3, 13, 13, 13);
		VoxelShape top = Block.createCuboidShape(2, 13, 2, 14, 16, 14);
		SHAPE = VoxelShapes.union(bottom, middle, top);
	}
}
