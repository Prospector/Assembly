package teamreborn.assembly.block;

import teamreborn.assembly.block.base.MachineBaseBlock;
import teamreborn.assembly.blockentity.GrinderBlockEntity;
import teamreborn.assembly.util.block.MachineBlockProperties;

public class GrinderBlock extends MachineBaseBlock {
	public GrinderBlock(Builder builder) {
		super(new MachineBlockProperties(GrinderBlockEntity::new), builder);
	}
}
