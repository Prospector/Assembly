package com.terraformersmc.assembly.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class AssemblyFallingBlock extends FallingBlock {
	public AssemblyFallingBlock(Settings settings) {
		super(settings);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return state.getTopMaterialColor(world, pos).color;
	}
}
