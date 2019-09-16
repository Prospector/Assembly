package teamreborn.assembly.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.github.prospector.silk.fluid.FluidContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import teamreborn.assembly.api.SapSource;
import teamreborn.assembly.block.base.AssemblyBlockWithEntity;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.util.block.AssemblyProperties;

import java.util.Map;

public class TreeTapBlock extends AssemblyBlockWithEntity {
	private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.createCuboidShape(6.5D, 1.0D, 0.0D, 10.5D, 5.5D, 6.5D),
		Direction.SOUTH, Block.createCuboidShape(16 - 6.5D, 1.0D, 16 - 0.0D, 16 - 10.5D, 5.5D, 16 - 6.5D).simplify(),
		Direction.WEST, Block.createCuboidShape(0.0D, 1.0D, 5.5D, 6.5D, 5.5D, 9.5D),
		Direction.EAST, Block.createCuboidShape(16 - 0.0D, 1.0D, 16 - 5.5D, 16 - 6.5D, 5.5D, 16 - 9.5D)));

	public TreeTapBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(AssemblyProperties.POURING, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return getBoundingShapeForState(state);
	}

	public static VoxelShape getBoundingShapeForState(BlockState state) {
		return BOUNDING_SHAPES.get(state.get(Properties.HORIZONTAL_FACING));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1) {
		super.neighborUpdate(state, world, pos, neighborBlock, neighborPos, boolean_1);
		if (!(world.getBlockEntity(pos.down()) instanceof FluidContainer)) {
			if (state.get(AssemblyProperties.POURING)) {
				world.setBlockState(pos, state.with(AssemblyProperties.POURING, false));
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(Properties.HORIZONTAL_FACING, AssemblyProperties.POURING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new TreeTapBlockEntity();
	}

	@Override
	public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
		Direction side = state.get(Properties.HORIZONTAL_FACING);
		BlockState placedOn = world.getBlockState(pos.offset(side));
		Block placedOnBlock = placedOn.getBlock();
		return placedOnBlock instanceof SapSource && ((SapSource) placedOnBlock).isSideSapSource(world, pos, placedOn, side.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.getSide().getOpposite();
		return Direction.Type.HORIZONTAL.method_10182(direction) ? getDefaultState().with(Properties.HORIZONTAL_FACING, direction) : null;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
	}
}
