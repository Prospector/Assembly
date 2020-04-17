package com.terraformersmc.assembly.mixin.common.tag;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidTags.class)
public interface FluidTagsInvoker {
	@Invoker("register")
	static Tag.Identified<Fluid> register(String id) {
		return null;
	}
}
