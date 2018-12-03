package teamreborn.assembly.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class GrinderBlock extends MachineBaseBlock {
	public GrinderBlock(Builder builder) {
		super(builder);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return null;
	}
}
