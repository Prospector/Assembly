package com.terraformersmc.assembly.screen;

import com.google.common.collect.Lists;
import com.terraformersmc.assembly.screen.builder.ScreenSyncerProvider;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AssemblyScreenHandlers {

	public static final Identifier BOILER = AssemblyConstants.Ids.BOILER;
	public static final Identifier FLUID_HOPPER = AssemblyConstants.Ids.FLUID_HOPPER;
	public static final Identifier BOILER_CHAMBER = AssemblyConstants.Ids.BOILER_CHAMBER;

	public static void registerServersideHandlers() {
		for (Identifier id : Lists.newArrayList(BOILER, FLUID_HOPPER, BOILER_CHAMBER)) {
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

	public static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player) {
		if (blockEntity.getWorld() != null && blockEntity.getWorld().getBlockEntity(blockEntity.getPos()) != blockEntity) {
			return false;
		} else {
			return player.squaredDistanceTo((double) blockEntity.getPos().getX() + 0.5D, (double) blockEntity.getPos().getY() + 0.5D, (double) blockEntity.getPos().getZ() + 0.5D) <= 64.0D;
		}
	}
}
