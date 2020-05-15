package com.terraformersmc.assembly;

import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.entity.AssemblyEntities;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.AssemblyItems;
import com.terraformersmc.assembly.loot.AssemblyLoot;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.serializer.AssemblyRecipeSerializers;
import com.terraformersmc.assembly.screen.AssemblyScreenSyncers;
import com.terraformersmc.assembly.sound.AssemblySoundEvents;
import com.terraformersmc.assembly.tag.AssemblyBlockTags;
import com.terraformersmc.assembly.tag.AssemblyFluidTags;
import com.terraformersmc.assembly.tag.AssemblyItemTags;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.world.AssemblyWorldgen;
import com.terraformersmc.assembly.world.feature.AssemblyFeatures;
import com.terraformersmc.assembly.world.feature.structure.AssemblyStructureFeatures;
import com.terraformersmc.assembly.world.feature.structure.AssemblyStructurePieceTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class Assembly implements ModInitializer {
	public static final String MOD_ID = AssemblyConstants.MOD_ID;
	public static final boolean DOSSIER_ENABLED = true;

	@Override
	public void onInitialize() {
		AssemblyNetworking.registerServersideHandlers();
		AssemblyFluids.register();
		AssemblyBlocks.register();
		AssemblyBlockEntities.register();
		AssemblyItems.register();
		AssemblyScreenSyncers.register();
		AssemblyRecipeTypes.register();
		AssemblyRecipeSerializers.register();
		AssemblyEntities.register();
		AssemblyFeatures.register();
		AssemblyStructureFeatures.register();
		AssemblyStructurePieceTypes.register();
		AssemblyWorldgen.register();
		AssemblyLoot.register();
		AssemblySoundEvents.register();
		AssemblyBlockTags.load();
		AssemblyFluidTags.load();
		AssemblyItemTags.load();
		FabricItemGroupBuilder.create(new Identifier(MOD_ID, "items")).icon(() -> new ItemStack(AssemblyItems.BRASS_GEAR)).appendItems(stacks -> Registry.ITEM.forEach(item -> {
			if (Registry.ITEM.getId(item).getNamespace().equals(MOD_ID)) {
				item.appendStacks(item.getGroup(), (DefaultedList<ItemStack>) stacks);
			}
		})).build();
	}
}
