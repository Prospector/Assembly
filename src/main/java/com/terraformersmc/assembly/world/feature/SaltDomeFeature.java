package com.terraformersmc.assembly.world.feature;

import com.mojang.datafixers.Dynamic;
import com.terraformersmc.assembly.structure.SaltDomeGenerator;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.block.Material;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.function.Function;

public class SaltDomeFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public SaltDomeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 0xabcdef;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return Start::new;
	}

	@Override
	public String getName() {
		return AssemblyConstants.Ids.SALT_DOME.toString();
	}

	@Override
	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return 40;
	}

	@Override
	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return 10;
	}

	@Override
	public int getRadius() {
		return 6;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			ChunkPos chunkPos = new ChunkPos(x, z);
			int xPos = chunkPos.getStartX() + this.random.nextInt(16);
			int zPos = chunkPos.getStartZ() + this.random.nextInt(16);
			int maxLevel = 35;
			int yPos = maxLevel - this.random.nextInt(15);
			BlockView blockView = chunkGenerator.getColumnSample(xPos, zPos);

			if (blockView.getBlockState(new BlockPos(xPos, yPos, zPos)).getMaterial() == Material.STONE) {
				SaltDomeGenerator.addPieces(structureManager, this.children, this.random, new BlockPos(xPos, yPos, zPos));
				this.setBoundingBoxFromChildren();
			}
		}
	}
}
