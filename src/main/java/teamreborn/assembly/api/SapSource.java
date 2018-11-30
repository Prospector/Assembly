package teamreborn.assembly.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Facing;

public interface SapSource {
	public boolean isSideSapSource(BlockState blockState, Facing side);
}
