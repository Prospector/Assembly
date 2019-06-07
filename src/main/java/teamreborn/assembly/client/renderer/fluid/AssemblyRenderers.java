package teamreborn.assembly.client.renderer.fluid;

import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.client.renderer.blockentityrenderer.TreeTapBlockEntityRenderer;
import teamreborn.assembly.client.renderer.blockentityrenderer.WoodenBarrelBlockEntityRenderer;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.fluid.AssemblyFluid;
import teamreborn.assembly.fluid.TexturedFluid;

import java.util.Map;

public class AssemblyRenderers {
	public static void register() {
		registerBlockEntityRenderers();
		registerFluidRenderers();
	}

	private static void registerBlockEntityRenderers() {
		BlockEntityRendererRegistry.INSTANCE.register(WoodenBarrelBlockEntity.class, new WoodenBarrelBlockEntityRenderer());
		BlockEntityRendererRegistry.INSTANCE.register(TreeTapBlockEntity.class, new TreeTapBlockEntityRenderer());
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
			FluidRenderHandlerRegistry.INSTANCE.register(still, (view, pos, state) -> new Sprite[] { MinecraftClient.getInstance().getSpriteAtlas().getSprite(((TexturedFluid) state.getFluid()).getStillTexture()),
				MinecraftClient.getInstance().getSpriteAtlas().getSprite(((TexturedFluid) state.getFluid()).getFlowingTexture()) });
			FluidRenderHandlerRegistry.INSTANCE.register(flowing, (view, pos, state) -> new Sprite[] { MinecraftClient.getInstance().getSpriteAtlas().getSprite(((TexturedFluid) state.getFluid()).getStillTexture()),
				MinecraftClient.getInstance().getSpriteAtlas().getSprite(((TexturedFluid) state.getFluid()).getFlowingTexture()) });
		}
	}
}
