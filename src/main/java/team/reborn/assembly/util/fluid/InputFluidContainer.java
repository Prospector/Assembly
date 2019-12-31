package team.reborn.assembly.util.fluid;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public interface InputFluidContainer extends AssemblyFluidContainer, FluidInsertable {

	// FluidInsertable
	@Override
	default FluidVolume attemptInsertion(FluidVolume fluid, Simulation simulation) {
		return getGroupedInv().attemptInsertion(fluid, simulation);
	}

	@Override
	default FluidAmount getMinimumAcceptedAmount() {
		return getGroupedInv().getMinimumAcceptedAmount();
	}

	@Override
	default FluidFilter getInsertionFilter() {
		return getGroupedInv().getInsertionFilter();
	}
}
