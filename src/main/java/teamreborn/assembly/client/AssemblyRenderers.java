package teamreborn.assembly.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.client.renderer.WoodenBarrelBlockEntityRenderer;

public class AssemblyRenderers implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(WoodenBarrelBlockEntity.class, new WoodenBarrelBlockEntityRenderer());
	}
}
