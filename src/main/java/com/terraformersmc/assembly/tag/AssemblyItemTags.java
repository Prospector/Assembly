package com.terraformersmc.assembly.tag;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.mixin.common.tag.ItemTagsInvoker;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class AssemblyItemTags {
	public static final Tag.Identified<Item> BRASS_INGOTS = ofCommon("brass_ingots");
	public static final Tag.Identified<Item> HEVEA_LOGS = ofAssembly("hevea_logs");

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
