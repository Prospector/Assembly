package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import com.terraformersmc.assembly.blockentity.FluidInjectorBlockEntity;
import com.terraformersmc.assembly.util.interaction.InteractionActionResult;
import com.terraformersmc.assembly.util.interaction.Interactions;
import com.terraformersmc.assembly.util.math.ShapeUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FluidInjectorBlock extends HorizontalFacingBlock implements BlockEntityProvider, AttributeProvider {
	private static final VoxelShape SHAPE_BASE = Block.createCuboidShape(0, 0, 0, 16, 10, 16);
	private static final VoxelShape SHAPE_PLATE = Block.createCuboidShape(2, 10, 2, 14, 11, 14);
	private static final VoxelShape SHAPE_LEFT_NOZZLE = Block.createCuboidShape(14, 10, 5, 16, 16, 11);
	private static final VoxelShape SHAPE_RIGHT_NOZZLE = Block.createCuboidShape(0, 10, 5, 2, 16, 11);
	private static final VoxelShape SHAPE_NORTH_SOUTH = VoxelShapes.union(SHAPE_BASE, SHAPE_PLATE, SHAPE_LEFT_NOZZLE, SHAPE_RIGHT_NOZZLE);
	private static final VoxelShape SHAPE_EAST_WEST = ShapeUtil.rotate90(SHAPE_NORTH_SOUTH, Direction.NORTH, Direction.EAST);

	public FluidInjectorBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FluidInjectorBlockEntity) {
			FluidInjectorBlockEntity fluidInjector = (FluidInjectorBlockEntity) blockEntity;
			to.offer(fluidInjector.getTank().getInsertable().getPureInsertable(), this.getOutlineShape(state, world, pos, null));
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return Interactions.handleDefaultInteractions(state, world, pos, player, hand, hit, (state1, world1, pos1, player1, hand1, hit1) -> {
			if (world instanceof ServerWorld) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof FluidInjectorBlockEntity) {
					FluidInjectorBlockEntity fluidInjector = ((FluidInjectorBlockEntity) blockEntity);
					ItemStack outputStack = fluidInjector.getStack(FluidInjectorBlockEntity.SLOT);
					if (!outputStack.isEmpty()) {
						if (!player.isCreative()) {
							player.inventory.insertStack(outputStack);
						}
						fluidInjector.removeStack(FluidInjectorBlockEntity.SLOT);
						return InteractionActionResult.SUCCESS;
					} else {
						ItemStack stackInHand = player.getStackInHand(hand);
						if (FluidAttributes.INSERTABLE.get(stackInHand) != RejectingFluidInsertable.NULL) {
							if (fluidInjector.canInsert(FluidInjectorBlockEntity.SLOT, stackInHand.copy().split(1), hit.getSide())) {
								ItemStack stack;
								if (!player.isCreative()) {
									stack = stackInHand.split(1);
								} else {
									stack = stackInHand.copy().split(1);
								}
								fluidInjector.setStack(FluidInjectorBlockEntity.SLOT, stack);
							}
						}
						return InteractionActionResult.SUCCESS;
					}
				}
			}
			return InteractionActionResult.PASS;
		});
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		if (direction == Direction.EAST || direction == Direction.WEST) {
			return SHAPE_EAST_WEST;
		}
		return SHAPE_NORTH_SOUTH;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new FluidInjectorBlockEntity();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction direction = context.getPlayerFacing().getOpposite();
		return this.getDefaultState().with(FACING, direction);
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
}
