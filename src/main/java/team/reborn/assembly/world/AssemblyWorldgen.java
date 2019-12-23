package team.reborn.assembly.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedStateProvider;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.block.HeveaLogBlock;
import team.reborn.assembly.tags.AssemblyBiomeSets;
import team.reborn.assembly.util.block.AssemblyProperties;

public class AssemblyWorldgen {
	public static final BranchedTreeFeatureConfig HEVEA_TREE_CONFIG;

	public static void register() {
		for (Biome biome : Registry.BIOME) {
			if (AssemblyBiomeSets.HEVEA_TREE_SPAWNING.contains(biome)) {
				biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.NORMAL_TREE.configure(HEVEA_TREE_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(15))));
			}
		}
	}

	static {
		BlockState heveaLog = AssemblyBlocks.HEVEA_LOG.getDefaultState().with(AssemblyProperties.ALIVE, true);
		WeightedStateProvider heveaStateProvider = new WeightedStateProvider().addState(heveaLog, 400);
		for (Direction direction : Direction.Type.HORIZONTAL) {
			heveaStateProvider.addState(heveaLog.with(HeveaLogBlock.getLatexProperty(direction), true), 10);
			for (Direction d2 : Direction.Type.HORIZONTAL) {
				heveaStateProvider.addState(heveaLog.with(HeveaLogBlock.getLatexProperty(direction), true).with(HeveaLogBlock.getLatexProperty(d2), true), 1);
			}
		}
		HEVEA_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
			heveaStateProvider,
			new SimpleStateProvider(AssemblyBlocks.HEVEA_LEAVES.getDefaultState()),
			new BlobFoliagePlacer(2, 0)
		).baseHeight(6).heightRandA(3).foliageHeight(3).noVines().build();
	}
}
