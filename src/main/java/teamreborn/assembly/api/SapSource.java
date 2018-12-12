package teamreborn.assembly.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;

public interface SapSource {
	public boolean isSideSapSource(ViewableWorld world, BlockPos pos, BlockState blockState, Direction side);
}
