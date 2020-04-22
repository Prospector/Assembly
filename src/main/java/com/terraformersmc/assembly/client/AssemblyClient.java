package com.terraformersmc.assembly.client;

import com.terraformersmc.assembly.client.gui.BaseScreen;
import com.terraformersmc.assembly.client.renderer.AssemblyRenderers;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import com.terraformersmc.assembly.screenhandler.AssemblyScreenHandlers;
import com.terraformersmc.assembly.screenhandler.builder.BuiltScreenHandler;
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
		ScreenProviderRegistry.INSTANCE.registerFactory(AssemblyScreenHandlers.BOILER, container -> {
			if (container instanceof BuiltScreenHandler) {
				return new BaseScreen((BuiltScreenHandler) container, "boiler");
			}
			return null;
		});
	}
}
