package teamreborn.assembly.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.client.renderer.TreeTapBlockEntityRenderer;
import teamreborn.assembly.client.renderer.WoodenBarrelBlockEntityRenderer;
import teamreborn.assembly.fluid.TexturedFluid;
import teamreborn.assembly.registry.AssemblyRegistry;

public class AssemblyRenderers implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(WoodenBarrelBlockEntity.class, new WoodenBarrelBlockEntityRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(TreeTapBlockEntity.class, new TreeTapBlockEntityRenderer());

		for (Identifier id : AssemblyRegistry.FLUIDS.keySet()) {
			Fluid fluid = AssemblyRegistry.FLUIDS.get(id);
			if (fluid instanceof TexturedFluid) {
				if (id.getPath().startsWith("flowing_")) {
					ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> registry.register(((TexturedFluid) fluid).getFlowingTexture()));
				} else {
					ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> registry.register(((TexturedFluid) fluid).getStillTexture()));

				}
				FluidRenderHandlerRegistry.INSTANCE.register(fluid, (view, pos, state) -> new Sprite[] { MinecraftClient.getInstance().getSpriteAtlas().getSprite(((TexturedFluid) state.getFluid()).getStillTexture()),
					MinecraftClient.getInstance().getSpriteAtlas().getSprite(((TexturedFluid) state.getFluid()).getFlowingTexture()) });
			}
		}
	}
}
