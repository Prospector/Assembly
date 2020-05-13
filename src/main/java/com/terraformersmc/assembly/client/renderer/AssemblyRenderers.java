package com.terraformersmc.assembly.client.renderer;

import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.client.renderer.blockentityrenderer.*;
import com.terraformersmc.assembly.entity.AssemblyEntities;
import com.terraformersmc.assembly.fluid.AssemblyFluid;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.fluid.TexturedFluid;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import java.util.Map;

public class AssemblyRenderers {
	public static void register() {
		registerEntityRenderers();
		registerBlockEntityRenderers();
		registerFluidRenderers();
	}

	private static void registerEntityRenderers() {
		EntityRendererRegistry.INSTANCE.register(AssemblyEntities.HEVEA_BOAT, (dispatcher, context) -> new BoatEntityRenderer(dispatcher));
	}

	private static void registerBlockEntityRenderers() {
		BlockEntityRendererRegistry.INSTANCE.register(AssemblyBlockEntities.FLUID_BARREL, FluidBarrelBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(AssemblyBlockEntities.TREE_TAP, TreeTapBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(AssemblyBlockEntities.PRESS, PressBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(AssemblyBlockEntities.SPIGOT, SpigotBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(AssemblyBlockEntities.INJECTOR, InjectorBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(AssemblyBlockEntities.TINKERING_TABLE, TinkeringTableBlockEntityRenderer::new);
	}

	private static void registerFluidRenderers() {
		Map<Identifier, AssemblyFluid> fluids = AssemblyFluids.getFluids();
		for (Identifier id : fluids.keySet()) {
			if (id.getPath().startsWith("flowing_")) {
				continue;
			}
			AssemblyFluid still = fluids.get(id);
			AssemblyFluid flowing = AssemblyFluids.getFlowing(still);
			ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> registry.register(((TexturedFluid) still).getStillTexture()));
			ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> registry.register(((TexturedFluid) still).getFlowingTexture()));
			FluidRenderHandlerRegistry.INSTANCE.register(still, (view, pos, state) -> new Sprite[]{MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(((TexturedFluid) state.getFluid()).getStillTexture()),
					MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(((TexturedFluid) state.getFluid()).getFlowingTexture())});
			FluidRenderHandlerRegistry.INSTANCE.register(flowing, (view, pos, state) -> new Sprite[]{MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(((TexturedFluid) state.getFluid()).getStillTexture()),
					MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(((TexturedFluid) state.getFluid()).getFlowingTexture())});
		}
	}
}
