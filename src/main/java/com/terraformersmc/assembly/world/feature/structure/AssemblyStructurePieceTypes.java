package com.terraformersmc.assembly.world.feature.structure;

import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssemblyStructurePieceTypes {
	private static final Map<Identifier, StructurePieceType> STRUCTURE_PIECE_TYPES = new LinkedHashMap<>();

	public static final StructurePieceType SALT_DOME = add(AssemblyConstants.Ids.SALT_DOME, SaltDomeGenerator.Piece::new);

	private static StructurePieceType add(Identifier id, StructurePieceType structurePieceType) {
		STRUCTURE_PIECE_TYPES.put(id, structurePieceType);
		return structurePieceType;
	}

	public static void register() {
		for (Identifier id : STRUCTURE_PIECE_TYPES.keySet()) {
			Registry.register(Registry.STRUCTURE_PIECE, id, STRUCTURE_PIECE_TYPES.get(id));
		}
	}
}
