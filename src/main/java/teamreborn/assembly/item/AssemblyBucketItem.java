package teamreborn.assembly.item;

import net.minecraft.item.BucketItem;
import teamreborn.assembly.fluid.AssemblyFluid;

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
