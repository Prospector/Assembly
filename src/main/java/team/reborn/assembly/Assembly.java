package team.reborn.assembly;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.blockentity.AssemblyBlockEntities;
import team.reborn.assembly.container.AssemblyContainers;
import team.reborn.assembly.entity.AssemblyEntities;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.loot.AssemblyLoot;
import team.reborn.assembly.network.AssemblyNetworking;
import team.reborn.assembly.util.AssemblyConstants;
import team.reborn.assembly.world.AssemblyWorldgen;
import team.reborn.assembly.world.feature.AssemblyFeatures;

public class Assembly implements ModInitializer {
	public static final String MOD_ID = AssemblyConstants.MOD_ID;

	@Override
	public void onInitialize() {
		AssemblyNetworking.register();
		AssemblyFluids.register();
		AssemblyItems.register();
		AssemblyBlocks.register();
		AssemblyBlockEntities.register();
		AssemblyContainers.register();
		AssemblyEntities.register();
		AssemblyFeatures.register();
		AssemblyWorldgen.register();
		AssemblyLoot.register();

		FabricItemGroupBuilder.create(new Identifier(MOD_ID, "items")).icon(AssemblyItems.OIL_BUCKET::getStackForRender).appendItems(stacks -> Registry.ITEM.forEach(item -> {
			if (Registry.ITEM.getId(item).getNamespace().equals(MOD_ID)) {
				item.appendStacks(item.getGroup(), (DefaultedList<ItemStack>) stacks);
			}
		})).build();
	}
}
