package teamreborn.assembly.tags;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;

public class AssemblyFluidTags {
	public static final Tag<Fluid> SAP = new Tag<>(new Identifier(Assembly.MOD_ID, "sap"));
	public static final Tag<Fluid> BIOMASS = new Tag<>(new Identifier(Assembly.MOD_ID, "biomass"));
}
