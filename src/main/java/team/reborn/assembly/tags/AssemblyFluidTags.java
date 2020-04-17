package team.reborn.assembly.tags;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.fluid.AssemblyFluid;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.mixin.common.tag.FluidTagsInvoker;
import team.reborn.assembly.util.AssemblyConstants;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFluidTags {
	private static final Map<AssemblyFluid, Tag.Identified<Fluid>> TAGS = new HashMap<>();

	public static final Tag.Identified<Fluid> LATEX = ofAssembly(AssemblyFluids.LATEX, "latex");
	public static final Tag.Identified<Fluid> STEAM = ofCommon(AssemblyFluids.STEAM, "steam");
	public static final Tag.Identified<Fluid> OIL = ofCommon(AssemblyFluids.CRUDE_OIL, "oil");

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
