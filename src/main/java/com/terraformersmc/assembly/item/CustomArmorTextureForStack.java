package com.terraformersmc.assembly.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public interface CustomArmorTextureForStack {
	default Identifier getId(ItemStack stack, boolean lowerParts, @Nullable String suffix) {
		return Registry.ITEM.getId(stack.getItem());
	}

	default String getArmorTexture(ItemStack stack, boolean lowerParts, @Nullable String suffix) {
		Identifier id = this.getId(stack, lowerParts, suffix);
		return id.getNamespace() + ":textures/entity/armor/" + id.getPath() + "_layer_" + (lowerParts ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png";
	}
}
