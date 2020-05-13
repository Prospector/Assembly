package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.blockentity.SqueezerBlockEntity;
import com.terraformersmc.assembly.util.interaction.Interaction;
import com.terraformersmc.assembly.util.interaction.InteractionActionResult;
import com.terraformersmc.assembly.util.interaction.Interactions;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
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


public class SqueezerBlock extends HorizontalFacingBlock implements BlockEntityProvider, AttributeProvider {

	public SqueezerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof SqueezerBlockEntity) {
			SqueezerBlockEntity steamPress = (SqueezerBlockEntity) blockEntity;
			to.offer(steamPress.getTank().getInsertable().getPureInsertable());
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return Interactions.handleDefaultInteractions(state, world, pos, player, hand, hit, Interaction.HANDLE_FLUIDS, (state1, world1, pos1, player1, hand1, hit1) -> {
			if (world != null) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SqueezerBlockEntity) {
					SqueezerBlockEntity steamPress = ((SqueezerBlockEntity) blockEntity);
					ItemStack outputStack = steamPress.getStack(SqueezerBlockEntity.INPUT_SLOT);
					if (!outputStack.isEmpty()) {
						if (!player.isCreative()) {
							player.inventory.insertStack(outputStack);
						}
						steamPress.removeStack(SqueezerBlockEntity.INPUT_SLOT);
						if (player instanceof ServerPlayerEntity) {
							player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
						}
						return InteractionActionResult.SUCCESS;
					} else {
						if (steamPress.canInsert(SqueezerBlockEntity.INPUT_SLOT, player.getStackInHand(hand).copy().split(1), hit.getSide())) {
							ItemStack stack;
							if (!player.isCreative()) {
								stack = player.getStackInHand(hand).split(1);
							} else {
								stack = player.getStackInHand(hand).copy().split(1);
							}
							steamPress.setStack(SqueezerBlockEntity.INPUT_SLOT, stack);
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
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && !player.isCreative()) {
			dropStacks(state, world, pos, null, player, player.getMainHandStack());
		}
		super.onBreak(world, pos, state, player);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.SQUEEZER.instantiate();
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateComparators(pos, this);
			}
		}
		super.onBlockRemoved(state, world, pos, newState, notify);
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}
}
