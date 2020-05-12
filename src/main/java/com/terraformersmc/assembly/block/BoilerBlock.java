package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import com.terraformersmc.assembly.screen.AssemblyScreenHandlers;
import com.terraformersmc.assembly.util.interaction.Interactions;
import com.terraformersmc.assembly.util.interaction.interactable.ScreenHandlerInteractable;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BoilerBlock extends HorizontalFacingBlock implements BlockEntityProvider, AttributeProvider, ScreenHandlerInteractable {
	public static final BooleanProperty LIT = Properties.LIT;

	public BoilerBlock(Settings settings) {
		super(settings.lightLevel((state) -> state.get(Properties.LIT) ? 13 : 0));
		this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false));
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BoilerBlockEntity) {
			to.offer(((BoilerBlockEntity) blockEntity).getInputTank().getInsertable().getPureInsertable(), VoxelShapes.fullCube());
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return Interactions.handleDefaultInteractions(state, world, pos, player, hand, hit);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AbstractFurnaceBlockEntity) {
				((AbstractFurnaceBlockEntity) blockEntity).setCustomName(stack.getName());
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BoilerBlockEntity) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateComparators(pos, this);
			}
			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIT, FACING);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.BOILER.instantiate();
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		super.neighborUpdate(state, world, pos, block, neighborPos, moved);
		if (pos.up().equals(neighborPos)) {
			if (world.getBlockState(neighborPos).getBlock() != AssemblyBlocks.BOILER_CHAMBER) {
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof BoilerBlockEntity) {
					ItemStack fuelStack = ((BoilerBlockEntity) blockEntity).getStack(0).copy();
					((BoilerBlockEntity) blockEntity).setStack(0, ItemStack.EMPTY);
					Text customName = ((BoilerBlockEntity) blockEntity).getCustomName();
					world.setBlockState(pos, Blocks.FURNACE.getDefaultState().with(AbstractFurnaceBlock.FACING, state.get(FACING)));
					blockEntity = world.getBlockEntity(pos);
					if (blockEntity instanceof FurnaceBlockEntity) {
						((FurnaceBlockEntity) blockEntity).setStack(1, fuelStack);
						((FurnaceBlockEntity) blockEntity).setCustomName(customName);
					}
				}
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public Item asItem() {
		return Blocks.FURNACE.asItem();
	}

	@Override
	public Identifier getScreenHandlerId() {
		return AssemblyScreenHandlers.BOILER;
	}
}
