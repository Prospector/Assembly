package com.terraformersmc.assembly.item;

import com.terraformersmc.assembly.fluid.AssemblyFluid;
import net.minecraft.item.BucketItem;

public class AssemblyBucketItem extends BucketItem {
	private final AssemblyFluid fluid;

	public AssemblyBucketItem(AssemblyFluid fluid, Settings settings) {
		super(fluid, settings);
		this.fluid = fluid;
	}

	public AssemblyFluid getFluid() {
		return fluid;
	}
}
