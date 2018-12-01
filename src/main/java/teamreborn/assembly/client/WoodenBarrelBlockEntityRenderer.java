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
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

import static prospector.silk.util.RenderUtil.frac;

public class WoodenBarrelBlockEntityRenderer extends BlockEntityRenderer<WoodenBarrelBlockEntity> {

	@Override
	public void render(WoodenBarrelBlockEntity barrel, double x, double y, double z, float ticksDelta, int destroyStage) {
		if (barrel.currentFluid != null && barrel.currentFluid != Fluids.EMPTY) {
			final Tessellator tessellator = Tessellator.getInstance();
			final VertexBuffer buffer = tessellator.getVertexBuffer();
			buffer.setOffset(x, y, z);
			GlStateManager.disableLighting();
			Sprite sprite = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(barrel.currentFluid.getDefaultState().getBlockState()).getSprite();
			double height = (barrel.fluidAmount / (float) 1000 * 14) + 1;
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			buffer.vertex(frac(3), frac(height), frac(3)).texture(sprite.getMinU(), sprite.getMinV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.vertex(frac(3), frac(height), frac(13)).texture(sprite.getMaxU(), sprite.getMinV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.vertex(frac(13), frac(height), frac(13)).texture(sprite.getMaxU(), sprite.getMaxV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.vertex(frac(13), frac(height), frac(3)).texture(sprite.getMinU(), sprite.getMaxV()).color(1.0F, 1.0F, 1.0F, 1.0F).next();
			buffer.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GlStateManager.enableLighting();
		}
		super.render(barrel, x, y, z, ticksDelta, destroyStage);
	}

}
