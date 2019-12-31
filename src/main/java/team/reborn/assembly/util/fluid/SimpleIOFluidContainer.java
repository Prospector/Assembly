package team.reborn.assembly.util.fluid;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;

import java.util.function.Supplier;

public class SimpleIOFluidContainer extends BaseFluidContainer implements IOFluidContainer {
	public SimpleIOFluidContainer(int invSize, FluidAmount tankCapacity) {
		super(invSize, tankCapacity);
	}

	public SimpleIOFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier) {
		super(invSize, capacitySupplier);
	}
}
