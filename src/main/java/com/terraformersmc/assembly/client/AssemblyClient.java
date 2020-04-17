package com.terraformersmc.assembly.client;

import com.terraformersmc.assembly.client.renderer.AssemblyRenderers;
import net.fabricmc.api.ClientModInitializer;

public class AssemblyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AssemblyRenderers.register();
		AssemblyTextures.register();
		AssemblyRenderLayers.register();
		AssemblyModels.register();
	}
}
