package com.terraformersmc.assembly.util.fluid;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;

import java.util.function.Supplier;

public class SimpleIOFluidContainer extends BaseFluidContainer implements IOFluidContainer {
	public SimpleIOFluidContainer(int invSize, FluidAmount tankCapacity) {
		super(invSize, tankCapacity);
	}

	public SimpleIOFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier) {
		super(invSize, capacitySupplier);
	}

	public SimpleIOFluidContainer(int invSize, FluidAmount capacity, FluidFilter filter) {
		super(invSize, capacity, filter);
	}

	public SimpleIOFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier, FluidFilter filter) {
		super(invSize, capacitySupplier, filter);
	}

}
