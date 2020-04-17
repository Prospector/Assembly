package com.terraformersmc.assembly.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public interface CustomArmorTexture {
	default Identifier getId(ArmorItem armorItem, boolean lowerParts, @Nullable String suffix) {
		return Registry.ITEM.getId(armorItem);
	}

	default String getArmorTexture(ArmorItem armorItem, boolean lowerParts, @Nullable String suffix) {
		Identifier id = getId(armorItem, lowerParts, suffix);
		return id.getNamespace() + ":textures/entity/armor/" + id.getPath() + "_layer_" + (lowerParts ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png";
	}
}
