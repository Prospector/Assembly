package com.terraformersmc.assembly.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.terraformersmc.assembly.util.interaction.interactable.InteractionBypass;

public class InteractionBypassBlockItem extends BlockItem implements InteractionBypass {

	private InteractionBypass bypass;

	public <B extends Block & InteractionBypass> InteractionBypassBlockItem(B block, Settings settings) {
		super(block, settings);
		this.bypass = block;
	}

	@Override
	public boolean bypassesInteractions(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return this.bypass.bypassesInteractions(state, world, pos, player, hand, hit);
	}
}
