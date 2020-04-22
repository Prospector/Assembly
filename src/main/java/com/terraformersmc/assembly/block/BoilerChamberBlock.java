package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import com.terraformersmc.assembly.blockentity.BoilerChamberBlockEntity;
import com.terraformersmc.assembly.util.interaction.InteractionUtil;
import com.terraformersmc.assembly.util.interaction.interactable.InteractionBypass;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BoilerChamberBlock extends HorizontalFacingBlock implements BlockEntityProvider, Waterloggable, AttributeProvider, InteractionBypass {
	public static final VoxelShape SHAPE;

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public BoilerChamberBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return InteractionUtil.handleDefaultInteractions(state, world, pos, player, hand, hit);
	}

	@Override
	public boolean bypassesInteractions(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return state.getBlock() instanceof BoilerChamberBlock;
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BoilerChamberBlockEntity) {
			BoilerChamberBlockEntity chamber = (BoilerChamberBlockEntity) blockEntity;
			BoilerBlockEntity boiler = chamber.getBoiler();
			if (boiler != null) {
				to.offer(boiler.getOutputTank().getExtractable().getPureExtractable(), SHAPE);
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ePos) {
		return SHAPE;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockPos downPos = pos.down();
		furnaceToBoiler(world, downPos, world.getBlockState(downPos), world.getBlockEntity(downPos));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
		if (neighborPos.equals(pos.down())) {
			furnaceToBoiler(world, neighborPos, world.getBlockState(neighborPos), world.getBlockEntity(neighborPos));
		}
	}

	private static void furnaceToBoiler(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (state.getBlock() == Blocks.FURNACE && blockEntity instanceof FurnaceBlockEntity) {
			FurnaceBlockEntity furnace = ((FurnaceBlockEntity) blockEntity);
			ItemStack fuelStack = furnace.getStack(1).copy();
			Text customName = furnace.getCustomName();
			boolean empty = true;
			for (int i = 0; i < furnace.size(); i++) {
				if (i != 1 && !furnace.getStack(i).isEmpty()) {
					empty = false;
					break;
				}
			}
			if (empty) {
				furnace.setStack(1, ItemStack.EMPTY);
				world.setBlockState(pos, AssemblyBlocks.BOILER.getDefaultState().with(BoilerBlock.FACING, state.get(FurnaceBlock.FACING)));
				BlockEntity newBlockEntity = world.getBlockEntity(pos);
				if (newBlockEntity instanceof BoilerBlockEntity) {
					((BoilerBlockEntity) newBlockEntity).setStack(0, fuelStack);
					((BoilerBlockEntity) newBlockEntity).setCustomName(customName);
				}
			}

		}
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.BOILER_CHAMBER.instantiate();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState ourState, Direction ourFacing, BlockState otherState, IWorld world, BlockPos ourPos, BlockPos otherPos) {
		if (ourState.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(ourPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return ourState;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	static {
		VoxelShape bottom = Block.createCuboidShape(2, 0, 2, 14, 3, 14);
		VoxelShape middle = Block.createCuboidShape(3, 3, 3, 13, 13, 13);
		VoxelShape top = Block.createCuboidShape(2, 13, 2, 14, 16, 14);
		SHAPE = VoxelShapes.union(bottom, middle, top);
	}
}
