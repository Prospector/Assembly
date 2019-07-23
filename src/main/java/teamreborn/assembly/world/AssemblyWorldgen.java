package teamreborn.assembly.world;

import teamreborn.assembly.tags.AssemblyBiomeSets;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.FeatureConfig;
import teamreborn.assembly.world.feature.AssemblyFeatures;

public class AssemblyWorldgen {
	public static void register() {
		for (Biome biome : Registry.BIOME) {
			if (AssemblyBiomeSets.RUBBER_TREE_SPAWNING.contains(biome)) {
				biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.configureFeature(AssemblyFeatures.RUBBER_TREE, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP, new ChanceDecoratorConfig(15)));
			}
		}
	}
}
