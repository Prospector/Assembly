package com.terraformersmc.assembly.client;

import com.google.common.collect.Lists;
import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import com.terraformersmc.assembly.blockentity.FluidHopperBlockEntity;
import com.terraformersmc.assembly.client.renderer.AssemblyRenderers;
import com.terraformersmc.assembly.client.screen.BoilerScreen;
import com.terraformersmc.assembly.client.screen.base.BaseSyncedScreen;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import com.terraformersmc.assembly.screen.AssemblyScreenHandlers;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.screen.builder.ScreenSyncerProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AssemblyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AssemblyNetworking.registerClientsideHandlers();
		registerClientsideHandlers();
		AssemblyRenderers.register();
		AssemblyTextures.register();
		AssemblyRenderLayers.register();
		AssemblyModels.register();
	}


	public static void registerClientsideHandlers() {
		ScreenProviderRegistry.INSTANCE.registerFactory(AssemblyScreenHandlers.BOILER, screenHandler -> {
			if (screenHandler instanceof ScreenSyncer) {
				return new BoilerScreen((ScreenSyncer<BoilerBlockEntity>) screenHandler);
			}
			return null;
		});
		ScreenProviderRegistry.INSTANCE.registerFactory(AssemblyScreenHandlers.BOILER_CHAMBER, screenHandler -> {
			if (screenHandler instanceof ScreenSyncer) {
				return new BaseSyncedScreen<>((ScreenSyncer<FluidHopperBlockEntity>) screenHandler);
			}
			return null;
		});
		ScreenProviderRegistry.INSTANCE.registerFactory(AssemblyScreenHandlers.FLUID_HOPPER, screenHandler -> {
			if (screenHandler instanceof ScreenSyncer) {
				return new BaseSyncedScreen<>((ScreenSyncer<FluidHopperBlockEntity>) screenHandler);
			}
			return null;
		});
	}
}
