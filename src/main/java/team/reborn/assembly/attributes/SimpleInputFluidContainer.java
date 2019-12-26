package team.reborn.assembly.attributes;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;

import java.util.function.Supplier;

public class SimpleInputFluidContainer extends BaseFluidContainer implements InputFluidContainer {
	public SimpleInputFluidContainer(int invSize, FluidAmount tankCapacity) {
		super(invSize, tankCapacity);
	}

	public SimpleInputFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier) {
		super(invSize, capacitySupplier);
	}
}
