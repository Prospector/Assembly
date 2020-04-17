package com.terraformersmc.assembly.util.interaction;

import com.terraformersmc.assembly.util.interaction.interactable.InteractionBypass;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

public class InteractionUtil {
	public static ActionResult handleInteractions(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, Interaction... interactions) {
		Item mainItem = player.getStackInHand(Hand.MAIN_HAND).getItem();
		if (mainItem instanceof InteractionBypass) {
			if (((InteractionBypass) mainItem).bypassesInteractions(state, world, pos, player, hand, hit)) {
				return ActionResult.PASS;
			}
		}
		for (Interaction interaction : interactions) {
			InteractionActionResult result = interaction.interact(state, world, pos, player, hand, hit);
			if (result.cancels()) {
				return result.getActionResult();
			}
		}
		return ActionResult.PASS;
	}

	public static ActionResult handleDefaultInteractions(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, Interaction... interactions) {
		Interaction[] newInteractions = Arrays.copyOf(interactions, interactions.length + 2);
		newInteractions[interactions.length] = Interaction.HANDLE_FLUIDS;
		newInteractions[interactions.length + 1] = Interaction.OPEN_MENU;
		return handleInteractions(state, world, pos, player, hand, hit, newInteractions);
	}

	public static ActionResult handleDefaultInteractions(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return handleInteractions(state, world, pos, player, hand, hit, Interaction.HANDLE_FLUIDS, Interaction.OPEN_MENU);
	}
}
