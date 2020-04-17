package com.terraformersmc.assembly.tag;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.Arrays;
import java.util.Set;

public class AssemblyBiomeSets {
    public static final Set<Biome> HEVEA_TREE_SPAWNING = fromKeys("minecraft:forest", "minecraft:wooded_hills", "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:birch_forest", "minecraft:wooded_mountains", "minecraft:badlands_plateau", "minecraft:swamp");

    private static Set<Biome> fromKeys(String... keys) {
        return Arrays.stream(keys).map(key -> Registry.BIOME.get(new Identifier(key))).collect(ImmutableSet.toImmutableSet());
    }
}
