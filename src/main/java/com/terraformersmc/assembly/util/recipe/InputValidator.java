package com.terraformersmc.assembly.util.recipe;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.item.ItemStack;

public interface InputValidator {
	default boolean isValidInput(ItemStack stack){
		return false;
	}

	default boolean isValidInput(FluidVolume volume) {
		return false;
	}
}
