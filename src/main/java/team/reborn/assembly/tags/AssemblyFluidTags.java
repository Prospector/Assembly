package team.reborn.assembly.tags;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.fluid.AssemblyFluid;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.util.AssemblyConstants;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFluidTags {
	private static final Map<AssemblyFluid, Tag<Fluid>> TAGS = new HashMap<>();

	public static final Tag<Fluid> LATEX = ofAssembly(AssemblyFluids.LATEX, "latex");
	public static final Tag<Fluid> STEAM = ofCommon(AssemblyFluids.STEAM, "steam");

	public static Tag<Fluid> of(AssemblyFluid fluid, Identifier id) {
		Tag<Fluid> tag = new FluidTags.CachingTag(id);
		TAGS.put(fluid, tag);
		TAGS.put(AssemblyFluids.getInverse(fluid), tag);
		return tag;
	}

	public static Tag<Fluid> ofAssembly(AssemblyFluid fluid, String path) {
		return of(fluid, new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag<Fluid> ofCommon(AssemblyFluid fluid, String path) {
		return of(fluid, new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}

	public static Tag<Fluid> get(AssemblyFluid fluid) {
		return TAGS.get(fluid);
	}
}
