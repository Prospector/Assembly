package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.SapFluid;

public class AssemblyFluids implements ModInitializer {

	public static BaseFluid LATEX_FLOWING;
	public static BaseFluid LATEX;
	public static BaseFluid BIOMASS_FLOWING;
	public static BaseFluid BIOMASS;

	public static BaseFluid register(String name, BaseFluid fluid) {
		Registry.register(Registry.FLUIDS, Assembly.MOD_ID + ":" + name, fluid);
		return fluid;
	}

	@Override
	public void onInitialize() {
		LATEX_FLOWING = register("flowing_latex", new SapFluid.Flowing());
		LATEX = register("latex", new SapFluid.Still());
		BIOMASS_FLOWING = register("flowing_biomass", new SapFluid.Flowing());
		BIOMASS = register("biomass", new SapFluid.Still());
	}
}
