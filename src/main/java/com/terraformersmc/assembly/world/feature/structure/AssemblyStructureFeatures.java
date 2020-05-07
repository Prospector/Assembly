package com.terraformersmc.assembly.world.feature.structure;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.world.feature.AssemblyFeatures;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.HashMap;
import java.util.Map;

public class AssemblyStructureFeatures {

	private static final Map<Identifier, StructureFeature<?>> STRUCTURE_FEATURES = new HashMap<>();

	public static final StructureFeature<?> SALT_DOME = add("salt_dome", AssemblyFeatures.SALT_DOME);

	public static <F extends StructureFeature<?>> F add(String name, F structureFeature) {
		STRUCTURE_FEATURES.put(new Identifier(Assembly.MOD_ID, name), structureFeature);
		return structureFeature;
	}

	public static void register() {
		for (Identifier id : STRUCTURE_FEATURES.keySet()) {
			StructureFeature<?> structureFeature = STRUCTURE_FEATURES.get(id);
			Registry.register(Registry.STRUCTURE_FEATURE, id, structureFeature);
		}
	}
}
