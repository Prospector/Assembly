package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.SapFluid;

public class AssemblyFluids implements ModInitializer {

	public static final BaseFluid LATEX_FLOWING;
	public static final BaseFluid LATEX;
	public static final BaseFluid BIOMASS_FLOWING;
	public static final BaseFluid BIOMASS;

	static {
		LATEX_FLOWING = register("flowing_latex", new SapFluid.Flowing());
		LATEX = register("latex", new SapFluid.Still());
		BIOMASS_FLOWING = register("flowing_biomass", new SapFluid.Flowing());
		BIOMASS = register("biomass", new SapFluid.Still());
	}

	public static BaseFluid register(String name, BaseFluid fluid) {
		Registry.register(Registry.FLUIDS, Assembly.MOD_ID + ":" + name, fluid);
		return fluid;
	}

	@Override
	public void onInitialize() { }
}
