package teamreborn.assembly.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluids;
import org.lwjgl.opengl.GL11;
import teamreborn.assembly.blockentity.SappingBarrelBlockEntity;

public class SappingBarrelBlockEntityRenderer extends BlockEntityRenderer<SappingBarrelBlockEntity> {

	@Override
	public void render(SappingBarrelBlockEntity sappingBarrel, double x, double y, double z, float ticksDelta, int destroyStage) {
		if (sappingBarrel.currentFluid != null && sappingBarrel.currentFluid != Fluids.EMPTY) {
			final Tessellator tessellator = Tessellator.getInstance();
			final VertexBuffer buffer = tessellator.getVertexBuffer();
			buffer.setOffset(x, y, z);
			GlStateManager.disableLighting();
			Sprite sprite = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(sappingBarrel.currentFluid.getDefaultState().getBlockState()).getSprite();
			double height = (sappingBarrel.fluidAmount / (float) 1000 * 14) + 1;
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			buffer.vertex(frac(3), frac(height), frac(3)).texture(sprite.getMinU(), sprite.getMinV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.vertex(frac(3), frac(height), frac(13)).texture(sprite.getMaxU(), sprite.getMinV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.vertex(frac(13), frac(height), frac(13)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.vertex(frac(13), frac(height), frac(3)).texture(sprite.getMinU(), sprite.getMaxV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GlStateManager.enableLighting();
		}
		super.render(sappingBarrel, x, y, z, ticksDelta, destroyStage);
	}

	public static double frac(double d) {
		return (double) 1 / (double) 16 * d;
	}

	public static float frac(float d) {
		return (float) 1 / (float) 16 * d;
	}

}
