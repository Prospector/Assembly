package com.terraformersmc.assembly.loot;

import com.terraformersmc.assembly.item.AssemblyItems;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class AssemblyLoot {
	private static final Identifier CAVE_SPIDER = new Identifier("minecraft", "entities/cave_spider");

	public static void register() {
		LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
			if (CAVE_SPIDER.equals(id)) {
				FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
						.conditionally(KilledByPlayerLootCondition.builder())
						.conditionally(RandomChanceWithLootingLootCondition.builder(0.02F, (float) 1 / 3))
						.with(ItemEntry.builder(AssemblyItems.VENOMOUS_FANG));
				supplier.pool(poolBuilder);
			}
		});
	}
}
