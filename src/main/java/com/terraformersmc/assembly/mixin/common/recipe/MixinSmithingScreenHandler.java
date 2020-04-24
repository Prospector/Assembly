package com.terraformersmc.assembly.mixin.common.recipe;

import com.terraformersmc.assembly.item.AssemblyItems;
import net.minecraft.item.Item;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(SmithingScreenHandler.class)
public class MixinSmithingScreenHandler {
	@Shadow
	@Final
	@Mutable
	private static Map<Item, Item> RECIPES;

	static {
		RECIPES = new HashMap<>(RECIPES);
		RECIPES.put(AssemblyItems.DIAMOND_HAMMER, AssemblyItems.NETHERITE_HAMMER);
	}
}
