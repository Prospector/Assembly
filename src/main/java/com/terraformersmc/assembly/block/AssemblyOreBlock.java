package com.terraformersmc.assembly.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.OreBlock;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class AssemblyOreBlock extends OreBlock {

	private final Pair<Integer, Integer> experience;

	public AssemblyOreBlock(Settings settings) {
		super(settings);
		this.experience = null;
	}

	public AssemblyOreBlock(Settings settings, int minExp, int maxExp) {
		super(settings);
		this.experience = new Pair<>(minExp, maxExp);
	}

	@Override
	protected int getExperienceWhenMined(Random random) {
		return experience != null ? MathHelper.nextInt(random, experience.getFirst(), experience.getSecond()) : 0;
	}
}
