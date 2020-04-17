package com.terraformersmc.assembly.client.renderer.blockentityrenderer;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.blockentity.FluidBarrelBlockEntity;
import com.terraformersmc.assembly.util.math.MathUtil;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class FluidBarrelBlockEntityRenderer extends BlockEntityRenderer<FluidBarrelBlockEntity> {

	public FluidBarrelBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(FluidBarrelBlockEntity barrel, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		FluidVolume volume = barrel.getTank().getInvFluid(0);
		if (!volume.isEmpty()) {
			Fluid fluid = volume.getRawFluid();
			FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
			BlockPos pos = barrel.getPos();
			World world = barrel.getWorld();
			FluidState state = fluid.getDefaultState();
			Sprite sprite = handler.getFluidSprites(world, pos, state)[0];
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			double height = (volume.getAmount_F().asInexactDouble() / barrel.getTank().getCapacity(0).asInexactDouble() * 14) + 1;
			int color = handler.getFluidColor(world, pos, state);
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;

			VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());
			Matrix4f modelMatrix = matrices.peek().getModel();
			Matrix3f normalMatrix = matrices.peek().getNormal();

			buffer.vertex(modelMatrix, MathUtil.fracf(3), MathUtil.fracf(height), MathUtil.fracf(3)).color(r, g, b, 1F).texture(sprite.getFrameU(MathUtil.getXFromU(sprite, sprite.getMinU()) + 3), sprite.getFrameV(MathUtil.getYFromV(sprite, sprite.getMinV()) + 3)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
			buffer.vertex(modelMatrix, MathUtil.fracf(3), MathUtil.fracf(height), MathUtil.fracf(13)).color(r, g, b, 1F).texture(sprite.getFrameU(MathUtil.getXFromU(sprite, sprite.getMaxU()) - 3), sprite.getFrameV(MathUtil.getYFromV(sprite, sprite.getMinV()) + 3)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
			buffer.vertex(modelMatrix, MathUtil.fracf(13), MathUtil.fracf(height), MathUtil.fracf(13)).color(r, g, b, 1F).texture(sprite.getFrameU(MathUtil.getXFromU(sprite, sprite.getMaxU()) - 3), sprite.getFrameV(MathUtil.getYFromV(sprite, sprite.getMaxV()) - 3)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
			buffer.vertex(modelMatrix, MathUtil.fracf(13), MathUtil.fracf(height), MathUtil.fracf(3)).color(r, g, b, 1F).texture(sprite.getFrameU(MathUtil.getXFromU(sprite, sprite.getMinU()) + 3), sprite.getFrameV(MathUtil.getYFromV(sprite, sprite.getMaxV()) - 3)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
		}
	}
}
