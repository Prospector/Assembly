package team.reborn.assembly.fluid;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.Assembly;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFluids {

	private static final Map<Identifier, AssemblyFluid> FLUIDS = new HashMap<>();
	private static final BiMap<AssemblyFluid, AssemblyFluid> STILL_FLOWING_MAP = HashBiMap.create();

	public static AssemblyFluid LATEX = add(new AssemblyFluid.Settings("latex").tickRate(15));
	public static AssemblyFluid FLOWING_LATEX = getFlowing(LATEX);
//	public static AssemblyFluid BIOMASS = add(new AssemblyFluid.Settings("biomass").tickRate(8));
//	public static AssemblyFluid FLOWING_BIOMASS = getFlowing(BIOMASS);
	public static AssemblyFluid OIL = add(new AssemblyFluid.Settings("oil").tickRate(20));
	public static AssemblyFluid FLOWING_OIL = getFlowing(OIL);

	public static AssemblyFluid add(AssemblyFluid.Settings settings) {
		AssemblyFluid still = new AssemblyFluid.Still(settings);
		AssemblyFluid flowing = new AssemblyFluid.Flowing(settings);
		FLUIDS.put(new Identifier(Assembly.MOD_ID, settings.getName()), still);
		FLUIDS.put(new Identifier(Assembly.MOD_ID, "flowing_" + settings.getName()), flowing);
		STILL_FLOWING_MAP.put(still, flowing);
		return still;
	}

	public static AssemblyFluid getFlowing(AssemblyFluid still) {
		return STILL_FLOWING_MAP.get(still);
	}

	public static AssemblyFluid getStill(AssemblyFluid flowing) {
		return STILL_FLOWING_MAP.inverse().get(flowing);
	}

	public static AssemblyFluid getInverse(AssemblyFluid fluid) {
		return STILL_FLOWING_MAP.containsKey(fluid) ? STILL_FLOWING_MAP.get(fluid) : STILL_FLOWING_MAP.inverse().get(fluid);
	}

	public static void register() {
		for (Identifier id : FLUIDS.keySet()) {
			Registry.register(Registry.FLUID, id, FLUIDS.get(id));
		}
	}

	public static Map<Identifier, AssemblyFluid> getFluids() {
		return FLUIDS;
	}
}
