package com.terraformersmc.assembly.tag;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.fluid.AssemblyFluid;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.mixin.common.tag.FluidTagsInvoker;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFluidTags {
	private static final Map<AssemblyFluid, Tag.Identified<Fluid>> TAGS = new HashMap<>();

	public static final Tag.Identified<Fluid> LATEX = ofAssembly(AssemblyFluids.LATEX, "latex");
	public static final Tag.Identified<Fluid> STEAM = ofCommon(AssemblyFluids.STEAM, "steam");
	public static final Tag.Identified<Fluid> CRUDE_OIL = ofAssembly(AssemblyFluids.CRUDE_OIL, "crude_oil");
	public static final Tag.Identified<Fluid> FISH_OIL = ofAssembly(AssemblyFluids.FISH_OIL, "fish_oil");

	public static Tag.Identified<Fluid> of(AssemblyFluid fluid, Identifier id) {
		Tag.Identified<Fluid> tag = FluidTagsInvoker.register(id.toString());
		TAGS.put(fluid, tag);
		TAGS.put(AssemblyFluids.getInverse(fluid), tag);
		return tag;
	}

	public static Tag.Identified<Fluid> ofAssembly(AssemblyFluid fluid, String path) {
		return of(fluid, new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag.Identified<Fluid> ofCommon(AssemblyFluid fluid, String path) {
		return of(fluid, new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}

	public static Tag.Identified<Fluid> get(AssemblyFluid fluid) {
		return TAGS.get(fluid);
	}
}
