package team.reborn.assembly.client;

import net.fabricmc.api.ClientModInitializer;
import team.reborn.assembly.client.renderer.AssemblyRenderers;

public class AssemblyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AssemblyRenderers.register();
		AssemblyTextures.register();
		AssemblyRenderLayers.register();
		AssemblyModels.register();
	}
}
