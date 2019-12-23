package team.reborn.assembly.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import team.reborn.assembly.blockentity.AssemblyBlockEntities;
import team.reborn.assembly.item.AssemblyItems;

import javax.annotation.Nullable;


public class SteamPressBlock extends HorizontalFacingBlock implements BlockEntityProvider {

	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	private static final VoxelShape UPPER_SHAPE;
	public static final VoxelShape LOWER_SHAPE;

	public SteamPressBlock(Settings settings) {
		super(settings);
		setDefaultState(this.getStateManager().getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos pos = context.getBlockPos();
		return pos.getY() < 255 && context.getWorld().getBlockState(pos.up()).canReplace(context) ? this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER).with(FACING, state.get(FACING)), 3);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf half = state.get(HALF);
		BlockPos otherHalf = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
		BlockState otherState = world.getBlockState(otherHalf);
		if (otherState.getBlock() == this && otherState.get(HALF) != half) {
			world.setBlockState(otherHalf, Blocks.AIR.getDefaultState(), 35);
			world.playLevelEvent(player, 2001, otherHalf, Block.getRawIdFromState(otherState));
			if (!world.isClient && !player.isCreative()) {
				dropStacks(state, world, pos, null, player, player.getMainHandStack());
				dropStacks(otherState, world, otherHalf, null, player, player.getMainHandStack());
			}
		}

		super.onBreak(world, pos, state, player);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		return state.get(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF);
	}

	@Override
	public Item asItem() {
		return AssemblyItems.STEAM_PRESS;
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.STEAM_PRESS.instantiate();
	}

	static {
		VoxelShape baseBottom = Block.createCuboidShape(0, 0, 0, 16, 4, 16);
		VoxelShape baseMiddle = Block.createCuboidShape(1, 4, 1, 15, 6, 15);
		VoxelShape baseTop = Block.createCuboidShape(0, 6, 0, 16, 10, 16);
		VoxelShape platform = Block.createCuboidShape(2, 10, 2, 14, 11, 14);
		VoxelShape pillarNW = Block.createCuboidShape(0, 10, 0, 2, 22, 2);
		VoxelShape pillarNE = Block.createCuboidShape(14, 10, 0, 16, 22, 2);
		VoxelShape pillarSE = Block.createCuboidShape(14, 10, 14, 16, 22, 16);
		VoxelShape pillarSW = Block.createCuboidShape(0, 10, 14, 2, 22, 16);
		VoxelShape top = Block.createCuboidShape(0, 22, 0, 16, 26, 16);
		LOWER_SHAPE = VoxelShapes.union(baseBottom, baseMiddle, baseTop, platform, pillarNW, pillarNE, pillarSE, pillarSW, top);
		UPPER_SHAPE = LOWER_SHAPE.offset(0, -1, 0);
	}
}
