package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.blockentity.SteamPressBlockEntity;
import com.terraformersmc.assembly.item.AssemblyItems;
import com.terraformersmc.assembly.util.interaction.InteractionActionResult;
import com.terraformersmc.assembly.util.interaction.InteractionUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class SteamPressBlock extends HorizontalFacingBlock implements BlockEntityProvider, AttributeProvider {

	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	private static final VoxelShape UPPER_SHAPE;
	private static final VoxelShape LOWER_SHAPE;
	public static final VoxelShape ARM_SHAPE;

	public SteamPressBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState().with(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		if (state.get(HALF) == DoubleBlockHalf.LOWER) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof SteamPressBlockEntity) {
				SteamPressBlockEntity steamPress = (SteamPressBlockEntity) blockEntity;
				to.offer(steamPress.getTank().getInsertable().getPureInsertable(), LOWER_SHAPE);
			}
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return InteractionUtil.handleDefaultInteractions(state, world, pos, player, hand, hit, (state1, world1, pos1, player1, hand1, hit1) -> {
			if (world != null) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SteamPressBlockEntity) {
					SteamPressBlockEntity steamPress = ((SteamPressBlockEntity) blockEntity);
					ItemStack outputStack = steamPress.getStack(SteamPressBlockEntity.OUTPUT_SLOT);
					if (!outputStack.isEmpty()) {
						if (!player.isCreative()) {
							player.inventory.insertStack(outputStack);
						}
						steamPress.removeStack(SteamPressBlockEntity.OUTPUT_SLOT);
						if (player instanceof ServerPlayerEntity) {
							player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
						}
						return InteractionActionResult.SUCCESS;
					} else {
						if (steamPress.canInsert(SteamPressBlockEntity.INPUT_SLOT, player.getStackInHand(hand).copy().split(1), hit.getSide())) {
							ItemStack stack;
							if (!player.isCreative()) {
								stack = player.getStackInHand(hand).split(1);
							} else {
								stack = player.getStackInHand(hand).copy().split(1);
							}
							steamPress.setStack(SteamPressBlockEntity.INPUT_SLOT, stack);
							if (!stack.isEmpty()) {
								if (player instanceof ServerPlayerEntity) {
									player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.PLAYERS, 0.3F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.5F) * 2.0F);
								}
								return InteractionActionResult.SUCCESS;
							}
						}
					}
				}
			}
			return InteractionActionResult.PASS;
		});
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
			world.syncWorldEvent(player, 2001, otherHalf, Block.getRawIdFromState(otherState));
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
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		DoubleBlockHalf half = state.get(HALF);
		if (blockEntity instanceof SteamPressBlockEntity) {
			return VoxelShapes.union(half == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE, ((SteamPressBlockEntity) blockEntity).getArmVoxelShape(half));
		}
		return half == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
		DoubleBlockHalf half = state.get(HALF);
		return half == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
		DoubleBlockHalf half = state.get(HALF);
		return half == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF);
	}

	@Override
	public Item asItem() {
		return AssemblyItems.STEAM_PRESS;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.STEAM_PRESS.instantiate();
	}

	static {
		VoxelShape base = Block.createCuboidShape(0, 0, 0, 16, 10, 16);
		VoxelShape platform = Block.createCuboidShape(2, 10, 2, 14, 11, 14);
		VoxelShape pillarNW = Block.createCuboidShape(0, 10, 0, 2, 22, 2);
		VoxelShape pillarNE = Block.createCuboidShape(14, 10, 0, 16, 22, 2);
		VoxelShape pillarSE = Block.createCuboidShape(14, 10, 14, 16, 22, 16);
		VoxelShape pillarSW = Block.createCuboidShape(0, 10, 14, 2, 22, 16);
		VoxelShape top = Block.createCuboidShape(0, 22, 0, 16, 26, 16);
		LOWER_SHAPE = VoxelShapes.union(base, platform, pillarNW, pillarNE, pillarSE, pillarSW, top).simplify();
		UPPER_SHAPE = LOWER_SHAPE.offset(0, -1, 0);

		VoxelShape plate = Block.createCuboidShape(3, 4, 3, 13, 5, 13);
		VoxelShape arm = Block.createCuboidShape(6, 5, 6, 10, 16, 10);
		ARM_SHAPE = VoxelShapes.union(plate, arm).simplify();
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		DoubleBlockHalf half = state.get(HALF);
		if (state.getBlock() != newState.getBlock() && half == DoubleBlockHalf.LOWER) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateComparators(pos, this);
			}
		}
		super.onBlockRemoved(state, world, pos, newState, notify);
	}
}
