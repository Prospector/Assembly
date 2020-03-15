package team.reborn.assembly.entity;

import com.terraformersmc.terraform.entity.TerraformBoat;
import com.terraformersmc.terraform.entity.TerraformBoatEntity;
import com.terraformersmc.terraform.item.TerraformBoatItem;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.block.AssemblyBlocks;

import java.util.function.Supplier;

public class AssemblyEntities {
	public static EntityType<TerraformBoatEntity> HEVEA_BOAT;

	public static void register() {
		HEVEA_BOAT = registerBoat("hevea", AssemblyBlocks.HEVEA_PLANKS, BoatEntity.Type.SPRUCE, () -> HEVEA_BOAT);
	}

	private static EntityType<TerraformBoatEntity> registerBoat(String name, ItemConvertible planks, BoatEntity.Type vanilla, Supplier<EntityType<TerraformBoatEntity>> boatSupplier) {
		Identifier id = new Identifier(Assembly.MOD_ID, name + "_boat");
		Identifier skin = new Identifier(Assembly.MOD_ID, "textures/entity/boat/" + name + "_boat.png");
		Item item = Registry.register(Registry.ITEM, id, new TerraformBoatItem((world, x, y, z) -> {
			TerraformBoatEntity entity = boatSupplier.get().create(world);
			if (entity != null) {
				entity.setPos(x, y, z);
			}
			return entity;
		}, new Item.Settings().maxCount(1).group(ItemGroup.TRANSPORTATION)));
		TerraformBoat boat = new TerraformBoat(item.asItem(), planks.asItem(), skin, vanilla);

		EntityType<TerraformBoatEntity> type = FabricEntityTypeBuilder.<TerraformBoatEntity>create(
			EntityCategory.MISC, (entity, world) -> new TerraformBoatEntity(entity, world, boat))
			.size(EntityDimensions.fixed(1.375F, 0.5625F))
			.build();

		return Registry.register(Registry.ENTITY_TYPE, id, type);
	}
}
