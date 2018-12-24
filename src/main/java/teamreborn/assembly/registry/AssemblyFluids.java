package teamreborn.assembly.registry;

import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.SapFluid;

public class AssemblyFluids {

	public static BaseFluid LATEX_FLOWING = add("flowing_latex", new SapFluid.Flowing());
	public static BaseFluid LATEX = add("latex", new SapFluid.Still());
	public static BaseFluid BIOMASS_FLOWING = add("flowing_biomass", new SapFluid.Flowing());
	public static BaseFluid BIOMASS = add("biomass", new SapFluid.Still());

	public static BaseFluid add(String name, BaseFluid fluid) {
		AssemblyRegistry.FLUIDS.put(new Identifier(Assembly.MOD_ID, name), fluid);
		return fluid;
	}

	public static void loadClass() {}
}
