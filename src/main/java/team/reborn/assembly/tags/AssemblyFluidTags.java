package team.reborn.assembly.tags;

import team.reborn.assembly.fluid.AssemblyFluid;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.Assembly;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFluidTags {
	private static final Map<AssemblyFluid, Tag<Fluid>> TAGS = new HashMap<>();

	public static final Tag<Fluid> LATEX = add(AssemblyFluids.LATEX, new Identifier(Assembly.MOD_ID, "latex"));
	public static final Tag<Fluid> STEAM = add(AssemblyFluids.STEAM, new Identifier(Assembly.MOD_ID, "steam"));

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
