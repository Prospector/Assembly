package com.terraformersmc.assembly.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class AssemblyGlowingOreBlock extends RedstoneOreBlock {

	private final Pair<Integer, Integer> experience;

	public AssemblyGlowingOreBlock(Settings settings) {
		super(settings);
		this.experience = null;
	}

	public AssemblyGlowingOreBlock(Settings settings, int minExp, int maxExp) {
		super(settings);
		this.experience = new Pair<>(minExp, maxExp);
	}

	protected int getExperienceWhenMined(Random random) {
		return experience != null ? MathHelper.nextInt(random, experience.getFirst(), experience.getSecond()) : 0;
	}

	@Override
	public void onStacksDropped(BlockState state, World world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
			int i = this.getExperienceWhenMined(world.random);
			if (i > 0) {
				this.dropExperience(world, pos, i);
			}
		}

	}

}
