package teamreborn.assembly.client.renderer.blockentityrenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

import static io.github.prospector.silk.util.MathUtil.frac;

public class WoodenBarrelBlockEntityRenderer extends BlockEntityRenderer<WoodenBarrelBlockEntity> {

	@Override
	public void render(WoodenBarrelBlockEntity barrel, double x, double y, double z, float ticksDelta, int destroyStage) {
		if (barrel.fluidInstance != null && barrel.fluidInstance.getFluid() != Fluids.EMPTY) {
			GlStateManager.pushMatrix();
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder buffer = tessellator.getBufferBuilder();
			buffer.setOffset(x, y, z);
			GlStateManager.disableLighting();
			Fluid fluid = barrel.fluidInstance.getFluid();
			FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
			BlockPos pos = barrel.getPos();
			World world = getWorld();
			FluidState state = fluid.getDefaultState();
			Sprite sprite = handler.getFluidSprites(world, pos, state)[0];
			double height = (barrel.fluidInstance.getAmount() / (float) WoodenBarrelBlockEntity.CAPACITY * 14) + 1;
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			int color = handler.getFluidColor(world, pos, state);
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			buffer.vertex(frac(3), frac(height), frac(3)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 3), sprite.getV(sprite.getYFromV(sprite.getMinV()) + 3)).color(r, g, b, 1F).next();
			buffer.vertex(frac(3), frac(height), frac(13)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 3), sprite.getV(sprite.getYFromV(sprite.getMinV()) + 3)).color(r, g, b, 1F).next();
			buffer.vertex(frac(13), frac(height), frac(13)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 3), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - 3)).color(r, g, b, 1F).next();
			buffer.vertex(frac(13), frac(height), frac(3)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 3), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - 3)).color(r, g, b, 1F).next();
			buffer.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
		super.render(barrel, x, y, z, ticksDelta, destroyStage);
	}

}
