package team.reborn.assembly.attributes;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;

import java.util.function.Supplier;

public class SimpleOutputFluidContainer extends BaseFluidContainer implements OutputFluidContainer {
	public SimpleOutputFluidContainer(int invSize, FluidAmount tankCapacity) {
		super(invSize, tankCapacity);
	}

	public SimpleOutputFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier) {
		super(invSize, capacitySupplier);
	}
}
