package teamreborn.assembly.tags;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;

public class AssemblyFluidTags {
	public static final Tag<Fluid> LATEX = new Tag<>(new Identifier(Assembly.MOD_ID, "latex"));
	public static final Tag<Fluid> BIOMASS = new Tag<>(new Identifier(Assembly.MOD_ID, "biomass"));
}
