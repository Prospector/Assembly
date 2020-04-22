package com.terraformersmc.assembly.util.fluid;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidInvTankChangeListener;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.misc.Saveable;

public interface AssemblyFluidContainer extends FixedFluidInv, Saveable {
	@Override
	default FluidAmount getMaxAmount_F(int tank) {
		return this.getCapacity(tank);
	}

	void setCapacity(FluidAmount capacity);

	FluidAmount getCapacity(int tank);

	void setOwnerListener(FluidInvTankChangeListener ownerListener);

}
