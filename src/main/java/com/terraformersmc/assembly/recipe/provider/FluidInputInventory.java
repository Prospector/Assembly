package com.terraformersmc.assembly.recipe.provider;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.inventory.Inventory;

public interface FluidInputInventory extends Inventory {
	FluidVolume getFluidInput();
}
