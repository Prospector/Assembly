package teamreborn.assembly.client;

import teamreborn.assembly.client.renderer.AssemblyRenderers;
import net.fabricmc.api.ClientModInitializer;

public class AssemblyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AssemblyRenderers.register();
	}
}
