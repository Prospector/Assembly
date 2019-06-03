package teamreborn.assembly.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
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
			Sprite sprite = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(Fluids.FLOWING_WATER.getDefaultState().getBlockState()).getSprite();
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			int color = BiomeColors.getWaterColor(treeTap.getWorld(), treeTap.getPos());
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			double angle = 22.5;
			double topHeight = 4.5;
			double endOfFlow = 16 - 6;
			double bottomAngled = topHeight - (16 - endOfFlow) * Math.tan(Math.toRadians(angle));
			//Top stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()) + .55)).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()) + .55)).color(r, g, b, 1F).next();
			buffer.vertex(frac(16), frac(topHeight), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - 9 + .05)).color(r, g, b, 1F).next();
			buffer.vertex(frac(16), frac(topHeight), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - 9 + .05)).color(r, g, b, 1F).next();

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
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();

			//Back stream
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();

			//Left Stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7.5), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();

			//Left angled bit
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7.5), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled + 0.58 * Math.tan(Math.toRadians(angle))), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()) + 1)).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(7.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMinU()) + 7), sprite.getV(sprite.getYFromV(sprite.getMinV()) + 1)).color(r, g, b, 1F).next();

			//Right Stream
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7.5), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomStream), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomStream), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMaxV()) - (16 - streamHeight))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();

			//Right angled bit
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7.5), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()) + 1)).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()) + 1)).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow + 0.58), frac(bottomAngled + 0.58 * Math.tan(Math.toRadians(angle))), frac(9.5)).texture(sprite.getU(sprite.getXFromU(sprite.getMaxU()) - 7), sprite.getV(sprite.getYFromV(sprite.getMinV()))).color(r, g, b, 1F).next();

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
