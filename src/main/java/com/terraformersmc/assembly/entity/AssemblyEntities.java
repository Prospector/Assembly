package com.terraformersmc.assembly.entity;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.terraform.entity.TerraformBoat;
import com.terraformersmc.terraform.entity.TerraformBoatEntity;
import com.terraformersmc.terraform.item.TerraformBoatItem;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AssemblyEntities {
	private static final Map<EntityType<TerraformBoatEntity>, Item> BOAT_ITEMS = new HashMap<>();
	public static EntityType<TerraformBoatEntity> HEVEA_BOAT;

	public static void register() {
		HEVEA_BOAT = registerBoat("hevea", AssemblyBlocks.HEVEA_PLANKS, BoatEntity.Type.SPRUCE, () -> HEVEA_BOAT);
	}

	private static EntityType<TerraformBoatEntity> registerBoat(String name, ItemConvertible planks, BoatEntity.Type vanilla, Supplier<EntityType<TerraformBoatEntity>> boatSupplier) {
		Identifier id = new Identifier(Assembly.MOD_ID, name + "_boat");
		Identifier skin = new Identifier(Assembly.MOD_ID, "textures/entity/boat/" + name + ".png");
		Item item = Registry.register(Registry.ITEM, id, new TerraformBoatItem(boatSupplier, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)));
		TerraformBoat boat = new TerraformBoat(item.asItem(), planks.asItem(), skin, vanilla);

		EntityType<TerraformBoatEntity> type = FabricEntityTypeBuilder.<TerraformBoatEntity>create(
				SpawnGroup.MISC, (entity, world) -> new TerraformBoatEntity(entity, world, boat))
				.size(EntityDimensions.fixed(1.375F, 0.5625F))
				.build();

		BOAT_ITEMS.put(type, item);
		return Registry.register(Registry.ENTITY_TYPE, id, type);
	}

	public static Item getBoatItem(EntityType<TerraformBoatEntity> entity) {
		return BOAT_ITEMS.get(entity);
	}
}
