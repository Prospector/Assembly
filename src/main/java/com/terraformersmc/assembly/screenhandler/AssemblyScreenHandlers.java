package com.terraformersmc.assembly.screenhandler;

import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import com.terraformersmc.assembly.client.gui.BaseScreen;
import com.terraformersmc.assembly.screenhandler.builder.BuiltScreenHandler;
import com.terraformersmc.assembly.util.AssemblyConstants;

public class AssemblyScreenHandlers {

	public static final Identifier BOILER = AssemblyConstants.Ids.BOILER;

	public static void register() {
		ContainerProviderRegistry.INSTANCE.registerFactory(BOILER, (syncId, id, player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			BlockEntity blockEntity = player.world.getBlockEntity(pos);
			if (blockEntity instanceof BoilerBlockEntity) {
				return ((BoilerBlockEntity) blockEntity).createContainer(syncId, player.inventory);
			}
			return null;
		});
		ScreenProviderRegistry.INSTANCE.registerFactory(BOILER, container -> {
			if (container instanceof BuiltScreenHandler) {
				return new BaseScreen((BuiltScreenHandler) container, "boiler");
			}
			return null;
		});

	}

}
