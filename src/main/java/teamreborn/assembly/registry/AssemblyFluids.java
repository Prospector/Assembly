package teamreborn.assembly.registry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.registry.Registry;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.fluid.SapFluid;

public class AssemblyFluids implements ModInitializer {

	public static final BaseFluid SAP_FLOWING;
	public static final BaseFluid SAP;
	public static final BaseFluid BIOMASS_FLOWING;
	public static final BaseFluid BIOMASS;

	static {
		SAP_FLOWING = register("flowing_sap", new SapFluid.Flowing());
		SAP = register("sap", new SapFluid.Still());
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
