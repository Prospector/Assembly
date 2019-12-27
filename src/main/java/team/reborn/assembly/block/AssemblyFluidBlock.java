package team.reborn.assembly.block;

import net.minecraft.block.FluidBlock;
import team.reborn.assembly.fluid.AssemblyFluid;

public class AssemblyFluidBlock extends FluidBlock {
	private final AssemblyFluid fluid;

	public AssemblyFluidBlock(AssemblyFluid fluid, Settings settings) {
		super(fluid, settings);
		this.fluid = fluid;
	}

	public AssemblyFluid getFluid() {
		return fluid;
	}
}
