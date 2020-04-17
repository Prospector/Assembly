package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeProvider;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.terraformersmc.assembly.api.SapSource;
import com.terraformersmc.assembly.blockentity.TreeTapBlockEntity;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Map;

public class TreeTapBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final BooleanProperty POURING = AssemblyConstants.Properties.POURING;

	private static final Map<Direction, VoxelShape> SHAPE = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Block.createCuboidShape(6.5D, 1.0D, 0.0D, 10.5D, 5.5D, 6.5D).simplify(),
			Direction.SOUTH, Block.createCuboidShape(16 - 6.5D, 1.0D, 16 - 0.0D, 16 - 10.5D, 5.5D, 16 - 6.5D).simplify(),
			Direction.WEST, Block.createCuboidShape(0.0D, 1.0D, 5.5D, 6.5D, 5.5D, 9.5D).simplify(),
			Direction.EAST, Block.createCuboidShape(16 - 0.0D, 1.0D, 16 - 5.5D, 16 - 6.5D, 5.5D, 16 - 9.5D).simplify()));

	public TreeTapBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(POURING, false));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE.get(state.get(FACING));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean boolean_1) {
		super.neighborUpdate(state, world, pos, neighborBlock, neighborPos, boolean_1);
		BlockPos downPos = pos.down();
		Block downBlock = world.getBlockState(downPos).getBlock();
		if (!(downBlock instanceof AttributeProvider)) {
			if (state.get(POURING)) {
				world.setBlockState(pos, state.with(POURING, false));
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POURING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new TreeTapBlockEntity();
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		Direction side = state.get(FACING);
		BlockState placedOn = world.getBlockState(pos.offset(side));
		Block placedOnBlock = placedOn.getBlock();
		return placedOnBlock instanceof SapSource && ((SapSource) placedOnBlock).isSideSapSource(world, pos, placedOn, side.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.getSide().getOpposite();
		return Direction.Type.HORIZONTAL.test(direction) ? getDefaultState().with(FACING, direction) : null;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}
}
