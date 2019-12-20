package teamreborn.assembly.client.renderer.blockentityrenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

import static teamreborn.assembly.util.MathUtil.*;

public class WoodenBarrelBlockEntityRenderer extends BlockEntityRenderer<WoodenBarrelBlockEntity> {
	public WoodenBarrelBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(WoodenBarrelBlockEntity barrel, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (barrel.fluidInstance != null && barrel.fluidInstance.getFluid() != Fluids.EMPTY) {
//			final Tessellator tessellator = Tessellator.getInstance();
//			final BufferBuilder buffer = tessellator.getBuffer();
//			buffer.setOffset(x, y, z);
			Fluid fluid = barrel.fluidInstance.getFluid();
			FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
			BlockPos pos = barrel.getPos();
			World world = barrel.getWorld();
			FluidState state = fluid.getDefaultState();
			Sprite sprite = handler.getFluidSprites(world, pos, state)[0];
			MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			double height = (barrel.fluidInstance.getAmount().getRawValue() / (float) WoodenBarrelBlockEntity.CAPACITY.getRawValue() * 14) + 1;
//			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			int color = handler.getFluidColor(world, pos, state);
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;

			VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

			Matrix4f modelMatrix = matrices.peek().getModel();
			Matrix3f normalMatrix = matrices.peek().getNormal();
			buffer.vertex(modelMatrix, fracf(3), fracf(height), fracf(3)).color(r, g, b, 1F).texture(sprite.getFrameU(getXFromU(sprite, sprite.getMinU()) + 3), sprite.getFrameV(getYFromV(sprite, sprite.getMinV()) + 3)).light(0xf000f0).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
			buffer.vertex(modelMatrix, fracf(3), fracf(height), fracf(13)).color(r, g, b, 1F).texture(sprite.getFrameU(getXFromU(sprite, sprite.getMaxU()) - 3), sprite.getFrameV(getYFromV(sprite, sprite.getMinV()) + 3)).light(0xf000f0).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
			buffer.vertex(modelMatrix, fracf(13), fracf(height), fracf(13)).color(r, g, b, 1F).texture(sprite.getFrameU(getXFromU(sprite, sprite.getMaxU()) - 3), sprite.getFrameV(getYFromV(sprite, sprite.getMaxV()) - 3)).light(0xf000f0).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
			buffer.vertex(modelMatrix, fracf(13), fracf(height), fracf(3)).color(r, g, b, 1F).texture(sprite.getFrameU(getXFromU(sprite, sprite.getMinU()) + 3), sprite.getFrameV(getYFromV(sprite, sprite.getMaxV()) - 3)).light(0xf000f0).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
//			buffer.setOffset(0.0, 0.0, 0.0);
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.disableAlphaTest();
			GlStateManager.enableTexture();
			GlStateManager.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA.value, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA.value);
//			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
		}
	}

}
