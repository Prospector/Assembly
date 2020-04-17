package com.terraformersmc.assembly.world;

import com.terraformersmc.assembly.tag.AssemblyBiomeSets;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.block.HeveaLogBlock;
import com.terraformersmc.assembly.world.feature.AssemblyFeatures;

public class AssemblyWorldgen {
	public static final BranchedTreeFeatureConfig HEVEA_TREE_CONFIG;

	public static void register() {
		for (Biome biome : Registry.BIOME) {
			forEachBiome(biome);
		}
		RegistryEntryAddedCallback.event(Registry.BIOME).register((i, identifier, biome) -> forEachBiome(biome));
	}

	private static void forEachBiome(Biome biome) {
		biome.addStructureFeature(AssemblyFeatures.SALT_DOME.configure(FeatureConfig.DEFAULT));
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, AssemblyFeatures.SALT_DOME.configure(FeatureConfig.DEFAULT));

		if (AssemblyBiomeSets.HEVEA_TREE_SPAWNING.contains(biome)) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.NORMAL_TREE.configure(HEVEA_TREE_CONFIG).createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(new ChanceDecoratorConfig(15))));
		}
	}

	static {
		BlockState heveaLog = AssemblyBlocks.HEVEA_LOG.getDefaultState().with(HeveaLogBlock.ALIVE, true);
		WeightedBlockStateProvider heveaTrunkProvider = new WeightedBlockStateProvider().addState(heveaLog, 400);
		for (Direction direction : Direction.Type.HORIZONTAL) {
			heveaTrunkProvider.addState(heveaLog.with(HeveaLogBlock.getLatexProperty(direction), true), 10);
			for (Direction d2 : Direction.Type.HORIZONTAL) {
				heveaTrunkProvider.addState(heveaLog.with(HeveaLogBlock.getLatexProperty(direction), true).with(HeveaLogBlock.getLatexProperty(d2), true), 1);
			}
		}
		HEVEA_TREE_CONFIG = new BranchedTreeFeatureConfig.Builder(
				heveaTrunkProvider,
				new SimpleBlockStateProvider(AssemblyBlocks.HEVEA_LEAVES.getDefaultState()),
				new BlobFoliagePlacer(2, 0, 0, 0, 3),
				new StraightTrunkPlacer(4, 2, 0)
		).noVines().build();
	}
}
