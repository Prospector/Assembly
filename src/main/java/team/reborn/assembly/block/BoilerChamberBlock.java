package team.reborn.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import team.reborn.assembly.blockentity.AssemblyBlockEntities;
import team.reborn.assembly.blockentity.BoilerBlockEntity;
import team.reborn.assembly.blockentity.BoilerChamberBlockEntity;

import javax.annotation.Nullable;

public class BoilerChamberBlock extends HorizontalFacingBlock implements BlockEntityProvider, Waterloggable, AttributeProvider {

	public static final VoxelShape SHAPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public BoilerChamberBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof BoilerChamberBlockEntity) {
			BoilerChamberBlockEntity chamber = (BoilerChamberBlockEntity) be;
			BoilerBlockEntity boiler = chamber.getBoiler();
			if (boiler != null) {
				to.offer(boiler.getOutputTank(), SHAPE);
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
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

	public static void furnaceToBoiler(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (state.getBlock() == Blocks.FURNACE && blockEntity instanceof FurnaceBlockEntity) {
			FurnaceBlockEntity furnace = ((FurnaceBlockEntity) blockEntity);
			ItemStack fuelStack = furnace.getInvStack(1).copy();
			Text customName = furnace.getCustomName();
			boolean empty = true;
			for (int i = 0; i < furnace.getInvSize(); i++) {
				if (i != 1 && !furnace.getInvStack(i).isEmpty()) {
					empty = false;
					break;
				}
			}
			if (empty) {
				furnace.setInvStack(1, ItemStack.EMPTY);
				world.setBlockState(pos, AssemblyBlocks.BOILER.getDefaultState().with(BoilerBlock.FACING, state.get(FurnaceBlock.FACING)));
				BlockEntity newBlockEntity = world.getBlockEntity(pos);
				if (newBlockEntity instanceof BoilerBlockEntity) {
					((BoilerBlockEntity) newBlockEntity).setInvStack(0, fuelStack);
					((BoilerBlockEntity) newBlockEntity).setCustomName(customName);
				}
			}

		}
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
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
		return getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState ourState, Direction ourFacing, BlockState otherState, IWorld world, BlockPos ourPos, BlockPos otherPos) {
		if (ourState.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(ourPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return ourState;
	}

	static {
		VoxelShape bottom = Block.createCuboidShape(2, 0, 2, 14, 3, 14);
		VoxelShape middle = Block.createCuboidShape(3, 3, 3, 13, 13, 13);
		VoxelShape top = Block.createCuboidShape(2, 13, 2, 14, 16, 14);
		SHAPE = VoxelShapes.union(bottom, middle, top);
	}
}
