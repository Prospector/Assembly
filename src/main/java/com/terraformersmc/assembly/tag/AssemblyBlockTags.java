package com.terraformersmc.assembly.tag;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import com.terraformersmc.assembly.mixin.common.tag.BlockTagsInvoker;

public class AssemblyBlockTags {
	public static final Tag.Identified<Block> HEVEA_LOGS = ofAssembly("hevea_logs");
	public static final Tag.Identified<Block> COPPER_ORES = ofCommon("copper_ores");
	public static final Tag.Identified<Block> COPPER_BLOCKS = ofCommon("copper_blocks");
	public static final Tag.Identified<Block> ZINC_BLOCKS = ofCommon("zinc_blocks");
	public static final Tag.Identified<Block> BRASS_BLOCKS = ofCommon("brass_blocks");

	public static final Tag.Identified<Block> ORE_CONCENTRATE_BLOCKS = ofAssembly("ore_concentrate_blocks");
	public static final Tag.Identified<Block> IRON_CONCENTRATE_BLOCKS = ofAssembly("iron_concentrate_blocks");
	public static final Tag.Identified<Block> GOLD_CONCENTRATE_BLOCKS = ofAssembly("gold_concentrate_blocks");
	public static final Tag.Identified<Block> COPPER_CONCENTRATE_BLOCKS = ofAssembly("copper_concentrate_blocks");
	public static final Tag.Identified<Block> ZINC_CONCENTRATE_BLOCKS = ofAssembly("zinc_concentrate_blocks");
	public static final Tag.Identified<Block> BRASS_BLEND_BLOCKS = ofAssembly("brass_blend_blocks");

	public static Tag.Identified<Block> of(Identifier id) {
		return BlockTagsInvoker.register(id.toString());
	}

	public static Tag.Identified<Block> ofAssembly(String path) {
		return of(new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag.Identified<Block> ofCommon(String path) {
		return of(new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}
}
