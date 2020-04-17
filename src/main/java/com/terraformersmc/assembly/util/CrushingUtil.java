package com.terraformersmc.assembly.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.terraformersmc.assembly.block.AssemblyBlocks;

import java.util.Map;

public class CrushingUtil {

	protected static final Map<Block, BlockState> CRUSHED_BLOCKS = Maps.newHashMap(ImmutableMap.<Block, BlockState>builder()
			.put(Blocks.COAL_ORE, AssemblyBlocks.CRUSHED_COAL_ORE.getDefaultState())
			.put(Blocks.IRON_ORE, AssemblyBlocks.CRUSHED_IRON_ORE.getDefaultState())
			.put(Blocks.GOLD_ORE, AssemblyBlocks.CRUSHED_GOLD_ORE.getDefaultState())
			.put(Blocks.REDSTONE_ORE, AssemblyBlocks.CRUSHED_REDSTONE_ORE.getDefaultState())
			.put(Blocks.LAPIS_ORE, AssemblyBlocks.CRUSHED_LAPIS_ORE.getDefaultState())
			.put(Blocks.DIAMOND_ORE, AssemblyBlocks.CRUSHED_DIAMOND_ORE.getDefaultState())
			.put(Blocks.EMERALD_ORE, AssemblyBlocks.CRUSHED_EMERALD_ORE.getDefaultState())
			.put(Blocks.NETHER_GOLD_ORE, AssemblyBlocks.CRUSHED_NETHER_GOLD_ORE.getDefaultState())
			.put(Blocks.NETHER_QUARTZ_ORE, AssemblyBlocks.CRUSHED_NETHER_QUARTZ_ORE.getDefaultState())
			.build());

	public static ActionResult crushBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		PlayerEntity player = context.getPlayer();
		BlockState crushedState = CRUSHED_BLOCKS.get(state.getBlock());
		if (crushedState != null) {
			world.playSound(player, pos, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.2F, 0.6F);
			if (!world.isClient) {
				world.setBlockState(pos, crushedState, 0b1011);
				if (player != null) {
					context.getStack().damage(1, player, (playerEntity -> playerEntity.sendToolBreakStatus(context.getHand())));
				}
			}
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
