package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.terraformersmc.assembly.blockentity.InjectorBlockEntity;
import com.terraformersmc.assembly.screen.AssemblyScreenSyncers;
import com.terraformersmc.assembly.util.interaction.Interactions;
import com.terraformersmc.assembly.util.interaction.interactable.ScreenHandlerInteractable;
import com.terraformersmc.assembly.util.math.ShapeUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InjectorBlock extends HorizontalFacingBlock implements BlockEntityProvider, AttributeProvider, ScreenHandlerInteractable {
	private static final VoxelShape SHAPE_BASE = Block.createCuboidShape(0, 0, 0, 16, 10, 16);
	private static final VoxelShape SHAPE_PLATE = Block.createCuboidShape(2, 10, 2, 14, 11, 14);
	private static final VoxelShape SHAPE_LEFT_NOZZLE = Block.createCuboidShape(14, 10, 5, 16, 16, 11);
	private static final VoxelShape SHAPE_RIGHT_NOZZLE = Block.createCuboidShape(0, 10, 5, 2, 16, 11);
	private static final VoxelShape SHAPE_Z_AXIS = VoxelShapes.union(SHAPE_BASE, SHAPE_PLATE, SHAPE_LEFT_NOZZLE, SHAPE_RIGHT_NOZZLE);
	private static final VoxelShape SHAPE_X_AXIS = ShapeUtil.rotate90(SHAPE_Z_AXIS, Direction.NORTH, Direction.EAST);

	public InjectorBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof InjectorBlockEntity) {
			InjectorBlockEntity fluidInjector = (InjectorBlockEntity) blockEntity;
			to.offer(fluidInjector.getTank().getInsertable().getPureInsertable(), this.getOutlineShape(state, world, pos, null));
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return Interactions.handleDefaultInteractions(state, world, pos, player, hand, hit);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(FACING).getAxis() == Direction.Axis.X ? SHAPE_X_AXIS : SHAPE_Z_AXIS;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new InjectorBlockEntity();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateComparators(pos, this);
			}
			super.onBlockRemoved(state, world, pos, newState, notify);
		}
	}

	@Override
	public Identifier getScreenHandlerId() {
		return AssemblyScreenSyncers.INJECTOR;
	}
}
