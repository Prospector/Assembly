package teamreborn.assembly.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import teamreborn.assembly.blockentity.TreeTapBlockEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.util.block.AssemblyProperties;

import static io.github.prospector.silk.util.MathUtil.frac;

public class TreeTapBlockEntityRenderer extends BlockEntityRenderer<TreeTapBlockEntity> {

	@Override
	public void render(TreeTapBlockEntity treeTap, double x, double y, double z, float ticksDelta, int destroyStage) {
		if (treeTap.getWorld().getBlockState(treeTap.getPos()).get(AssemblyProperties.POURING)) {
			GlStateManager.pushMatrix();
			Direction facing = treeTap.getWorld().getBlockState(treeTap.getPos()).get(Properties.FACING_HORIZONTAL);
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder buffer = tessellator.getBufferBuilder();
			GlStateManager.disableLighting();
			Fluid fluid = treeTap.getPouringFluid();
			FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
			BlockPos pos = treeTap.getPos();
			World world = getWorld();
			FluidState state = fluid.getDefaultState();
			Sprite sprite = handler.getFluidSprites(world, pos, state)[1];
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			int color = handler.getFluidColor(world, pos, state);
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			double angle = 22.5;
			double topHeight = 4.5;
			double endOfFlow = 16 - 6;
			double radiansAngle = Math.toRadians(angle);
			double bottomAngled = topHeight - (16 - endOfFlow) * Math.tan(radiansAngle);
			double flowingMultiplier = 0.5;
			//Top stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU((sprite.getXFromU(sprite.getMinU()) + 7) * flowingMultiplier), sprite.getV((sprite.getYFromV(sprite.getMaxV()) + 0.44) * flowingMultiplier)).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU((sprite.getXFromU(sprite.getMaxU()) - 7) * flowingMultiplier), sprite.getV((sprite.getYFromV(sprite.getMaxV()) + 0.44) * flowingMultiplier)).color(r, g, b, 1F).next();
			buffer.vertex(frac(16), frac(topHeight), frac(9.5)).texture(sprite.getU((sprite.getXFromU(sprite.getMaxU()) - 7) * flowingMultiplier), sprite.getV((sprite.getYFromV(sprite.getMinV()) + 9.96) * flowingMultiplier)).color(r, g, b, 1F).next();
			buffer.vertex(frac(16), frac(topHeight), frac(7.5)).texture(sprite.getU((sprite.getXFromU(sprite.getMinU()) + 7) * flowingMultiplier), sprite.getV((sprite.getYFromV(sprite.getMinV()) + 9.96) * flowingMultiplier)).color(r, g, b, 1F).next();

			double bottomStream = 0;
			BlockEntity below = getWorld().getBlockEntity(treeTap.getPos().down());
			if (below instanceof WoodenBarrelBlockEntity) {
				bottomStream = -16 + (((WoodenBarrelBlockEntity) below).fluidInstance.getAmount() / (double) WoodenBarrelBlockEntity.CAPACITY * 14) + 1;
			}

			double streamHeight = bottomAngled - bottomStream;

			if (streamHeight > 16) {
				streamHeight = 16;
				// Slight stretching will occur, but I think that's better than rendering another quad
				// bottomStream = bottomAngled - 16;
			}

			//front stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();

			//Back stream
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();

			//Left Stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7.5)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();

			//Left angled bit
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7.5)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled + 0.58 * Math.tan(radiansAngle)), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMinV())))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMinV()) + 1))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMinU()) + 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMinV()) + 1))).color(r, g, b, 1F).next();

			//Right Stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7.5)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight)))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();

			//Right angled bit
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7.5)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMinV()) + 1))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * (sprite.getYFromV(sprite.getMinV()) + 1))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled + 0.58 * Math.tan(radiansAngle)), frac(9.5)).texture(sprite.getU(flowingMultiplier * (sprite.getXFromU(sprite.getMaxU()) - 7)), sprite.getV(flowingMultiplier * sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();

			GlStateManager.translated(x + 0.5, y + 0.5, z + 0.5);
			GlStateManager.rotated(270 - facing.asRotation(), 0.0, 1.0, 0.0);
			GlStateManager.translated(-0.5, -0.5, -0.5);
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
		super.render(treeTap, x, y, z, ticksDelta, destroyStage);
	}
}
