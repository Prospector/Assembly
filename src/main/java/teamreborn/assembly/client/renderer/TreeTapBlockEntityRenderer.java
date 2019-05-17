package teamreborn.assembly.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
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
			buffer.setOffset(x, y, z);
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
			double bottomHeight = topHeight - (16 - endOfFlow) * Math.tan(Math.toRadians(angle));
			buffer.vertex(frac(endOfFlow), frac(bottomHeight), frac(7.5)).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1F).next();
			buffer.vertex(frac(endOfFlow), frac(bottomHeight), frac(9.5)).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1F).next();
			buffer.vertex(frac(16), frac(topHeight), frac(9.5)).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1F).next();
			buffer.vertex(frac(16), frac(topHeight), frac(7.5)).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1F).next();
			buffer.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
		super.render(treeTap, x, y, z, ticksDelta, destroyStage);
	}

}
