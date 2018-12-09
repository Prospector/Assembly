package teamreborn.assembly.block;

import teamreborn.assembly.block.base.MachineAssemblyBlock;
import teamreborn.assembly.blockentity.GrinderBlockEntity;
import teamreborn.assembly.util.block.BlockWithEntitySettings;

public class GrinderBlock extends MachineAssemblyBlock {
	public GrinderBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockWithEntitySettings createMachineSettings() {
		return new BlockWithEntitySettings(GrinderBlockEntity::new);
	}
}
