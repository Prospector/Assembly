package team.reborn.assembly.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public interface SapSource {
	boolean isSideSapSource(WorldView world, BlockPos pos, BlockState blockState, Direction side);
}
