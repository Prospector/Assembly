package team.reborn.assembly.world;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleStateProvider;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.tags.AssemblyBiomeSets;

public class AssemblyWorldgen {
	public static final BranchedTreeFeatureConfig HEVEA_TREE_CONFIG = createOakLikeConfig(AssemblyBlocks.HEVEA_LOG, AssemblyBlocks.HEVEA_LEAVES);

	public static void register() {
		for (Biome biome : Registry.BIOME) {
			if (AssemblyBiomeSets.HEVEA_TREE_SPAWNING.contains(biome)) {
				biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.NORMAL_TREE.configure(HEVEA_TREE_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(15))));
			}
		}
	}

	private static BranchedTreeFeatureConfig createOakLikeConfig(Block trunk, Block leaves) {
		return new BranchedTreeFeatureConfig.Builder(
			new SimpleStateProvider(trunk.getDefaultState()),
			new SimpleStateProvider(leaves.getDefaultState()),
			new BlobFoliagePlacer(2, 0)
		).baseHeight(6).heightRandA(3).foliageHeight(3).noVines().build();
	}
}
