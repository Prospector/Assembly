package com.terraformersmc.assembly.util.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public interface OutputFluidContainer extends AssemblyFluidContainer, FluidExtractable {

	// FluidExtractable
	@Override
	default FluidVolume attemptExtraction(FluidFilter filter, FluidAmount maxAmount, Simulation simulation) {
		return this.getGroupedInv().attemptExtraction(filter, maxAmount, simulation);
	}

	@Override
	default FluidVolume attemptAnyExtraction(FluidAmount maxAmount, Simulation simulation) {
		return this.getGroupedInv().attemptAnyExtraction(maxAmount, simulation);
	}
}
