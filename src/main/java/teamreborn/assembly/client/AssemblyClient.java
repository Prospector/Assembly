package teamreborn.assembly.client;

import net.fabricmc.api.ClientModInitializer;
import teamreborn.assembly.client.renderer.AssemblyRenderers;
import teamreborn.assembly.client.textures.AssemblyTextures;

public class AssemblyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AssemblyRenderers.register();
		AssemblyTextures.register();
	}
}
