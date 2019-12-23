package team.reborn.assembly.world.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import team.reborn.assembly.Assembly;

import java.util.HashMap;
import java.util.Map;

public class AssemblyFeatures {

	private static final Map<Identifier, Feature<? extends FeatureConfig>> FEATURES = new HashMap<>();

	public static <F extends Feature<? extends FeatureConfig>> F add(String name, F feature) {
		FEATURES.put(new Identifier(Assembly.MOD_ID, name), feature);
		return feature;
	}

	public static void register() {
		for (Identifier id : FEATURES.keySet()) {
			Registry.register(Registry.FEATURE, id, FEATURES.get(id));
		}
	}
}
