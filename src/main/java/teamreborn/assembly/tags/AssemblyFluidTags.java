package teamreborn.assembly.tags;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.AssemblyFluid;
import teamreborn.assembly.fluid.AssemblyFluids;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFluidTags {
	private static final Map<AssemblyFluid, Tag<Fluid>> TAGS = new HashMap<>();

	public static final Tag<Fluid> LATEX = add(AssemblyFluids.LATEX, new Identifier(Assembly.MOD_ID, "latex"));
	public static final Tag<Fluid> BIOMASS = add(AssemblyFluids.BIOMASS, new Identifier(Assembly.MOD_ID, "biomass"));
	public static final Tag<Fluid> OIL = add(AssemblyFluids.OIL, new Identifier(Assembly.MOD_ID, "oil"));

	private static Tag<Fluid> add(AssemblyFluid fluid, Identifier id) {
		Tag<Fluid> tag = new Tag<>(id);
		TAGS.put(fluid, tag);
		TAGS.put(AssemblyFluids.getInverse(fluid), tag);
		return tag;
	}

	public static Tag<Fluid> get(AssemblyFluid fluid) {
		return TAGS.get(fluid);
	}
}
