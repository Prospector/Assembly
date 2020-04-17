package com.terraformersmc.assembly.fluid;

import net.minecraft.util.Identifier;

public interface TexturedFluid {
	Identifier getFlowingTexture();

	Identifier getStillTexture();
}
