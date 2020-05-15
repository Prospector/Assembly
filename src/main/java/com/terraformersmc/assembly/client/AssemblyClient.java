package com.terraformersmc.assembly.client;

import com.terraformersmc.assembly.client.renderer.AssemblyRenderers;
import com.terraformersmc.assembly.client.screen.AssemblySyncedScreens;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import net.fabricmc.api.ClientModInitializer;

public class AssemblyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AssemblyNetworking.registerClientsideHandlers();
		AssemblySyncedScreens.register();
		AssemblyRenderers.register();
		AssemblyTextures.register();
		AssemblyRenderLayers.register();
		AssemblyModels.register();
	}
}
