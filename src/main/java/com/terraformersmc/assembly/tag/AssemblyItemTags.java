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
	public static final Tag.Identified<Item> BRASS_PLATES = ofCommon("brass_plates");
	public static final Tag.Identified<Item> COPPER_ORES = ofCommon("copper_ores");
	public static final Tag.Identified<Item> COPPER_BLOCKS = ofCommon("copper_blocks");
	public static final Tag.Identified<Item> ZINC_BLOCKS = ofCommon("zinc_blocks");
	public static final Tag.Identified<Item> BRASS_BLOCKS = ofCommon("brass_blocks");
	public static final Tag.Identified<Item> HEVEA_LOGS = ofAssembly("hevea_logs");
	public static final Tag.Identified<Item> HAMMERS = ofAssembly("hammers");

	public static Tag.Identified<Item> of(Identifier id) {
		return ItemTagsInvoker.register(id.toString());
	}

	public static Tag.Identified<Item> ofAssembly(String path) {
		return of(new Identifier(Assembly.MOD_ID, path));
	}

	public static Tag.Identified<Item> ofCommon(String path) {
		return of(new Identifier(AssemblyConstants.COMMON_NAMESPACE, path));
	}
}
