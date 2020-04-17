package com.terraformersmc.assembly.util.interaction;

import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.terraformersmc.assembly.util.interaction.interactable.ScreenHandlerInteractable;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;
import com.terraformersmc.assembly.util.interaction.interactable.TankOutputInteractable;

public interface Interaction {
	Interaction HANDLE_FLUIDS = (state, world, pos, player, hand, hit) -> {
		FluidInsertable insertable = RejectingFluidInsertable.NULL;
		FluidExtractable extractable = EmptyFluidExtractable.NULL;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof TankInputInteractable) {
			insertable = ((TankInputInteractable) blockEntity).getInteractableInsertable();
		}
		if (blockEntity instanceof TankOutputInteractable) {
			extractable = ((TankOutputInteractable) blockEntity).getInteractableExtractable();
		}
		if (FluidVolumeUtil.interactWithTank(insertable, extractable, player, hand)) {
			return InteractionActionResult.SUCCESS;
		} else {
			return InteractionActionResult.PASS;
		}
	};
	Interaction OPEN_MENU = (state, world, pos, player, hand, hit) -> {
		Block block = state.getBlock();
		if (block instanceof ScreenHandlerInteractable) {
			if (!world.isClient) {
				ContainerProviderRegistry.INSTANCE.openContainer(((ScreenHandlerInteractable) block).getScreenHandlerId(), player, buf -> buf.writeBlockPos(pos));
			}
			return InteractionActionResult.SUCCESS;
		}
		return InteractionActionResult.PASS;
	};

	InteractionActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);
}
