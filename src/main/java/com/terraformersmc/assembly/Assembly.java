package com.terraformersmc.assembly;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.entity.AssemblyEntities;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.AssemblyItems;
import com.terraformersmc.assembly.loot.AssemblyLoot;
import com.terraformersmc.assembly.network.AssemblyNetworking;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.screenhandler.AssemblyScreenHandlers;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.world.AssemblyWorldgen;
import com.terraformersmc.assembly.world.feature.AssemblyFeatures;

public class Assembly implements ModInitializer {
	public static final String MOD_ID = AssemblyConstants.MOD_ID;
	public static final boolean DOSSIER_ENABLED = true;

	@Override
	public void onInitialize() {
		AssemblyNetworking.register();
		AssemblyFluids.register();
		AssemblyBlocks.register();
		AssemblyBlockEntities.register();
		AssemblyItems.register();
		AssemblyScreenHandlers.register();
		AssemblyRecipeTypes.register();
		AssemblyRecipeSerializers.register();
		AssemblyEntities.register();
		AssemblyFeatures.register();
		AssemblyWorldgen.register();
		AssemblyLoot.register();

		FabricItemGroupBuilder.create(new Identifier(MOD_ID, "items")).icon(AssemblyItems.BRASS_GEAR::getStackForRender).appendItems(stacks -> Registry.ITEM.forEach(item -> {
			if (Registry.ITEM.getId(item).getNamespace().equals(MOD_ID)) {
				item.appendStacks(item.getGroup(), (DefaultedList<ItemStack>) stacks);
			}
		})).build();
	}
}
