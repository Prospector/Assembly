package com.terraformersmc.assembly.util.interaction;

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidInvUtil;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.misc.PlayerInvUtil;
import com.terraformersmc.assembly.util.interaction.interactable.ScreenHandlerInteractable;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;
import com.terraformersmc.assembly.util.interaction.interactable.TankOutputInteractable;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Interaction {
	Interaction HANDLE_FLUIDS = (state, world, pos, player, hand, hit) -> {
		FluidInsertable insertable = RejectingFluidInsertable.NULL;
		FluidExtractable extractable = EmptyFluidExtractable.NULL;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		boolean interactable = false;
		if (blockEntity instanceof TankInputInteractable) {
			insertable = ((TankInputInteractable) blockEntity).getInteractableInsertable();
			interactable = true;
		}
		if (blockEntity instanceof TankOutputInteractable) {
			extractable = ((TankOutputInteractable) blockEntity).getInteractableExtractable();
			interactable = true;
		}
		if (!interactable) {
			return InteractionActionResult.PASS;
		}
		ItemStack stack = player.getStackInHand(hand);
		if (FluidAttributes.EXTRACTABLE.get(stack) != EmptyFluidExtractable.NULL) {
			if (!world.isClient()) {
				FluidInvUtil.interactWithTank(insertable, extractable, player, PlayerInvUtil.referenceHand(player, hand));
			}
			return InteractionActionResult.SUCCESS;
		}
		return InteractionActionResult.PASS;
	};
	Interaction OPEN_SCREEN_HANDLER = (state, world, pos, player, hand, hit) -> {
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
