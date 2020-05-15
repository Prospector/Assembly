package com.terraformersmc.assembly.screen;

import com.terraformersmc.assembly.screen.builder.ScreenSyncerProvider;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class AssemblyScreenSyncers {

	private static final Set<Identifier> IDS = new HashSet<>();

	public static final Identifier BOILER = add(AssemblyConstants.Ids.BOILER);
	public static final Identifier FLUID_HOPPER = add(AssemblyConstants.Ids.FLUID_HOPPER);
	public static final Identifier BOILER_CHAMBER = add(AssemblyConstants.Ids.BOILER_CHAMBER);
	public static final Identifier TINKERING_TABLE = add(AssemblyConstants.Ids.TINKERING_TABLE);
	public static final Identifier SQUEEZER = add(AssemblyConstants.Ids.SQUEEZER);
	public static final Identifier INJECTOR = add(AssemblyConstants.Ids.INJECTOR);

	public static void register() {
		for (Identifier id : IDS) {
			ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, identifier, player, buf) -> {
				BlockPos pos = buf.readBlockPos();
				BlockEntity blockEntity = player.world.getBlockEntity(pos);
				if (blockEntity instanceof ScreenSyncerProvider) {
					return ((ScreenSyncerProvider<?>) blockEntity).createSyncer(syncId, player.inventory);
				}
				return null;
			});
		}
	}

	private static Identifier add(Identifier id) {
		IDS.add(id);
		return id;
	}

	public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player) {
		if (blockEntity.getWorld() != null && blockEntity.getWorld().getBlockEntity(blockEntity.getPos()) != blockEntity) {
			return false;
		} else {
			return player.squaredDistanceTo((double) blockEntity.getPos().getX() + 0.5D, (double) blockEntity.getPos().getY() + 0.5D, (double) blockEntity.getPos().getZ() + 0.5D) <= 64.0D;
		}
	}
}
