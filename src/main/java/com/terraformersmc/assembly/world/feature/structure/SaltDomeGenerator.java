package com.terraformersmc.assembly.world.feature.structure;

import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class SaltDomeGenerator {
	private static final String[] VARIANTS = new String[]{"large"};

	public static void addPieces(StructureManager manager, List<StructurePiece> pieces, Random random, BlockPos pos) {
		BlockRotation blockRotation = BlockRotation.random(random);
		pieces.add(new SaltDomeGenerator.Piece(manager, new Identifier(AssemblyConstants.Ids.SALT_DOME + "/" + Util.getRandom(VARIANTS, random)), pos, blockRotation));
	}

	public static class Piece extends SimpleStructurePiece {
		private final Identifier template;
		private final BlockRotation structureRotation;

		public Piece(StructureManager manager, Identifier template, BlockPos pos, BlockRotation rotation) {
			super(AssemblyStructurePieceTypes.SALT_DOME, 0);
			this.template = template;
			this.pos = pos;
			this.structureRotation = rotation;
			this.initializeStructureData(manager);
		}

		public Piece(StructureManager manager, CompoundTag tag) {
			super(AssemblyStructurePieceTypes.SALT_DOME, tag);
			this.template = new Identifier(tag.getString("Template"));
			this.structureRotation = BlockRotation.valueOf(tag.getString("Rot"));
			this.initializeStructureData(manager);
		}

		private void initializeStructureData(StructureManager manager) {
			Structure structure = manager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = (new StructurePlacementData()).setRotation(this.structureRotation).setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putString("Template", this.template.toString());
			tag.putString("Rot", this.structureRotation.name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, WorldAccess world, Random random, BlockBox boundingBox) {
		}

		@Override
		public boolean generate(ServerWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
			return super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
		}
	}
}
