package team.reborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import team.reborn.assembly.util.AssemblyConstants;
import team.reborn.assembly.util.math.ShapeUtil;

public class ConveyorBeltBlock extends HorizontalFacingBlock {
	public static final BooleanProperty MOVING = AssemblyConstants.Properties.MOVING;

	public ConveyorBeltBlock(Settings settings) {
		super(settings);
		setDefaultState(this.getStateManager().getDefaultState().with(MOVING, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		VoxelShape leftRail = Block.createCuboidShape(0, 0, 0, 2, 3, 16);
		VoxelShape belt = Block.createCuboidShape(2, 0, 0, 14, 2, 16);
		VoxelShape rightRail = Block.createCuboidShape(14, 0, 0, 16, 3, 16);
		return ShapeUtil.rotate90(VoxelShapes.union(leftRail, belt, rightRail), Direction.NORTH, state.get(FACING), Direction.Axis.Y);
	}

	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(MOVING, FACING);
	}
}
