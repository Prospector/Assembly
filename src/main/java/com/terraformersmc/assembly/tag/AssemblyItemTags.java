package com.terraformersmc.assembly.tag;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.mixin.common.tag.ItemTagsInvoker;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class AssemblyItemTags {
	public static final Tag.Identified<Item> COPPER_INGOTS = ofCommon("copper_ingots");
	public static final Tag.Identified<Item> ZINC_INGOTS = ofCommon("zinc_ingots");
	public static final Tag.Identified<Item> BRASS_INGOTS = ofCommon("brass_ingots");
	public static final Tag.Identified<Item> BRASS_PLATES = ofAssembly("brass_plates");
	public static final Tag.Identified<Item> COPPER_ORES = ofCommon("copper_ores");
	public static final Tag.Identified<Item> COPPER_BLOCKS = ofCommon("copper_blocks");
	public static final Tag.Identified<Item> ZINC_BLOCKS = ofCommon("zinc_blocks");
	public static final Tag.Identified<Item> BRASS_BLOCKS = ofCommon("brass_blocks");
	public static final Tag.Identified<Item> HEVEA_LOGS = ofAssembly("hevea_logs");
	public static final Tag.Identified<Item> HAMMERS = ofAssembly("hammers");
	public static final Tag.Identified<Item> FLATTENED_FISHES = ofAssembly("flattened_fishes");
	public static final Tag.Identified<Item> SQUISHY = ofAssembly("squishy");
	public static final Tag.Identified<Item> POPPABLE = ofAssembly("poppable");

	public static final Tag.Identified<Item> IRON_CONCENTRATES = ofAssembly("iron_concentrates");
	public static final Tag.Identified<Item> GOLD_CONCENTRATES = ofAssembly("gold_concentrates");
	public static final Tag.Identified<Item> COPPER_CONCENTRATES = ofAssembly("copper_concentrates");
	public static final Tag.Identified<Item> ZINC_CONCENTRATES = ofAssembly("zinc_concentrates");
	public static final Tag.Identified<Item> BRASS_BLENDS = ofAssembly("brass_blends");

	public static final Tag.Identified<Item> IRON_CONCENTRATE_BLOCKS = ofAssembly("iron_concentrate_blocks");
	public static final Tag.Identified<Item> GOLD_CONCENTRATE_BLOCKS = ofAssembly("gold_concentrate_blocks");
	public static final Tag.Identified<Item> COPPER_CONCENTRATE_BLOCKS = ofAssembly("copper_concentrate_blocks");
	public static final Tag.Identified<Item> ZINC_CONCENTRATE_BLOCKS = ofAssembly("zinc_concentrate_blocks");
	public static final Tag.Identified<Item> BRASS_BLEND_BLOCKS = ofAssembly("brass_blend_blocks");

	private static Tag.Identified<Item> of(Identifier id) {
		return ItemTagsInvoker.register(id.toString());
	}

	private static Tag.Identified<Item> ofAssembly(String path) {
		return of(new Identifier(Assembly.MOD_ID, path));
	}

	private static Tag.Identified<Item> ofCommon(String path) {
		return of(new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}

	public static void load() {

	}
}
