package teamreborn.assembly.world;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.FeatureConfig;
import teamreborn.assembly.tags.AssemblyBiomeSets;
import teamreborn.assembly.world.feature.AssemblyFeatures;

public class AssemblyWorldgen {
	public static void register() {
		for (Biome biome : Registry.BIOME) {
			if (AssemblyBiomeSets.HEVEA_TREE_SPAWNING.contains(biome)) {
				biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.configureFeature(AssemblyFeatures.HEVEA_TREE, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP, new ChanceDecoratorConfig(15)));
			}
		}
	}
}
