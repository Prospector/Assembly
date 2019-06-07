package teamreborn.assembly;

import net.fabricmc.api.ModInitializer;
import teamreborn.assembly.block.AssemblyBlocks;
import teamreborn.assembly.blockentity.AssemblyBlockEntities;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.item.AssemblyItems;
import teamreborn.assembly.world.feature.AssemblyFeatures;

public class Assembly implements ModInitializer {
	public static final String MOD_ID = "assembly";

	@Override
	public void onInitialize() {
		AssemblyFluids.register();
		AssemblyItems.register();
		AssemblyBlocks.register();
		AssemblyBlockEntities.register();
		AssemblyFeatures.register();
	}
}
