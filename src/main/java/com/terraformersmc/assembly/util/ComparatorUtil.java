package com.terraformersmc.assembly.util;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ComparatorUtil {
	public static int calculateItemContainerOutput(Inventory itemContainer) {
		if (itemContainer == null) {
			return 0;
		} else {
			int itemCount = 0;
			float f = 0.0F;

			for (int slot = 0; slot < itemContainer.size(); ++slot) {
				ItemStack itemStack = itemContainer.getStack(slot);
				if (!itemStack.isEmpty()) {
					f += (float) itemStack.getCount() / (float) Math.min(itemContainer.getMaxCountPerStack(), itemStack.getMaxCount());
					++itemCount;
				}
			}

			f /= (float) itemContainer.size();
			return MathHelper.floor(f * 14.0F) + (itemCount > 0 ? 1 : 0);
		}
	}


	public static int calculateFluidContainerOutput(IOFluidContainer fluidContainer) {
		return calculateFluidContainerOutput(fluidContainer, -1);
	}

	public static int calculateFluidContainerOutput(IOFluidContainer fluidContainer, int... tanks) {
		if (fluidContainer == null || tanks.length < 1) {
			return 0;
		} else {
			FluidAmount totalFluidAmount = FluidAmount.ZERO;
			FluidAmount totalCapacity = FluidAmount.ZERO;

			if (tanks[0] == -1) { // Calculate for every tank
				for (int tank = 0; tank < fluidContainer.getTankCount(); tank++) {
					FluidVolume volume = fluidContainer.getInvFluid(tank);
					totalFluidAmount = totalFluidAmount.add(volume.getAmount_F());
					totalCapacity = totalCapacity.add(fluidContainer.getCapacity(tank));
				}
			} else {
				for (int tank : tanks) {
					FluidVolume volume = fluidContainer.getInvFluid(tank);
					totalFluidAmount = totalFluidAmount.add(volume.getAmount_F());
					totalCapacity = totalCapacity.add(fluidContainer.getCapacity(tank));
				}
			}
			if (totalFluidAmount == FluidAmount.ZERO || totalCapacity == FluidAmount.ZERO) {
				return 0;
			}
			return MathHelper.floor(totalFluidAmount.div(totalCapacity).mul(15).asInexactDouble());
		}
	}
}
