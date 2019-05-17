package teamreborn.assembly.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.client.renderer.TreeTapBlockEntityRenderer;
import teamreborn.assembly.client.renderer.WoodenBarrelBlockEntityRenderer;

public class AssemblyRenderers implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(WoodenBarrelBlockEntity.class, new WoodenBarrelBlockEntityRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(TreeTapBlockEntity.class, new TreeTapBlockEntityRenderer());
	}
}
