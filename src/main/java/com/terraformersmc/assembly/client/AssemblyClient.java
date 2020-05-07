package com.terraformersmc.assembly.client;

import com.terraformersmc.assembly.client.renderer.AssemblyRenderers;
import com.terraformersmc.assembly.client.screen.BoilerScreen;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import com.terraformersmc.assembly.screen.AssemblyScreenHandlers;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

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
				return new BoilerScreen((ScreenSyncer) screenHandler);
			}
			return null;
		});
	}
}
