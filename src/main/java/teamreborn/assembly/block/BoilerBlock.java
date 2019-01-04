package teamreborn.assembly.block;

import teamreborn.assembly.block.base.AssemblyBlockWithEntity;
import teamreborn.assembly.blockentity.BoilerBlockEntity;
import teamreborn.assembly.util.block.BlockWithEntitySettings;

public class BoilerBlock extends AssemblyBlockWithEntity {
	public BoilerBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockWithEntitySettings createMachineSettings() {
		return new BlockWithEntitySettings(BoilerBlockEntity::new);
	}
}
