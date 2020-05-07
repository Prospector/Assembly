package com.terraformersmc.assembly.screen;

import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AssemblyScreenHandlers {

	public static final Identifier BOILER = AssemblyConstants.Ids.BOILER;

	public static void registerServersideHandlers() {
		ContainerProviderRegistry.INSTANCE.registerFactory(BOILER, (syncId, id, player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			BlockEntity blockEntity = player.world.getBlockEntity(pos);
			if (blockEntity instanceof BoilerBlockEntity) {
				return ((BoilerBlockEntity) blockEntity).createContainer(syncId, player.inventory);
			}
			return null;
		});
	}
}
