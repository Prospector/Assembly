package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.blockentity.FluidHopperBlockEntity;
import com.terraformersmc.assembly.screen.AssemblyScreenHandlers;
import com.terraformersmc.assembly.util.ComparatorUtil;
import com.terraformersmc.assembly.util.interaction.Interactions;
import com.terraformersmc.assembly.util.interaction.interactable.ScreenHandlerInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FluidHopperBlock extends HopperBlock implements AttributeProvider, ScreenHandlerInteractable {
	public FluidHopperBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FluidHopperBlockEntity) {
			FluidHopperBlockEntity fluidHopper = (FluidHopperBlockEntity) blockEntity;
			to.offer(fluidHopper.getFluidTank(), VoxelShapes.fullCube());
		}
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return AssemblyBlockEntities.FLUID_HOPPER.instantiate();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FluidHopperBlockEntity) {
				((FluidHopperBlockEntity) blockEntity).setCustomName(itemStack.getName());
			}
		}

	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return Interactions.handleDefaultInteractions(state, world, pos, player, hand, hit);
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FluidHopperBlockEntity) {
				world.updateComparators(pos, this);
			}

			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FluidHopperBlockEntity) {
			((FluidHopperBlockEntity) blockEntity).onEntityCollided(entity);
		}
	}

	@Override
	public Identifier getScreenHandlerId() {
		return AssemblyScreenHandlers.FLUID_HOPPER;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FluidHopperBlockEntity) {
			return ComparatorUtil.calculateFluidContainerOutput(((FluidHopperBlockEntity) blockEntity).getFluidTank());
		}
		return 0;
	}
}
