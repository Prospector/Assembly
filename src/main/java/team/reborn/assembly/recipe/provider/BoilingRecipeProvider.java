package team.reborn.assembly.recipe.provider;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.inventory.Inventory;

public interface BoilingRecipeProvider extends Inventory {
	FluidVolume getFluidInput();
}
