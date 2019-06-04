package teamreborn.assembly.registry;

import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.LatexFluid;

public class AssemblyFluids {

	public static BaseFluid FLOWING_LATEX = add("flowing_latex", new LatexFluid.Flowing());
	public static BaseFluid LATEX = add("latex", new LatexFluid.Still());
	public static BaseFluid FLOWING_BIOMASS = add("flowing_biomass", new LatexFluid.Flowing());
	public static BaseFluid BIOMASS = add("biomass", new LatexFluid.Still());

	public static BaseFluid add(String name, BaseFluid fluid) {
		AssemblyRegistry.FLUIDS.put(new Identifier(Assembly.MOD_ID, name), fluid);
		return fluid;
	}

	public static void loadClass() {}
}
