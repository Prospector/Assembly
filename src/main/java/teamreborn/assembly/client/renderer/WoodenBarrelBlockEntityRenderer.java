package teamreborn.assembly.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BiomeColors;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import org.lwjgl.opengl.GL11;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

import static io.github.prospector.silk.util.RenderUtil.frac;

public class WoodenBarrelBlockEntityRenderer extends BlockEntityRenderer<WoodenBarrelBlockEntity> {

	@Override
	public void render(WoodenBarrelBlockEntity barrel, double x, double y, double z, float ticksDelta, int destroyStage) {
		if (barrel.fluidInstance != null && barrel.fluidInstance.getFluid() != Fluids.EMPTY) {
			final Tessellator tessellator = Tessellator.getInstance();
			final BufferBuilder buffer = tessellator.getBufferBuilder();
			buffer.setOffset(x, y, z);
			GlStateManager.disableLighting();
			Sprite sprite = MinecraftClient.getInstance().getBakedModelManager().getBlockStateMaps().getModel(barrel.fluidInstance.getFluid().getDefaultState().getBlockState()).getSprite();
			double height = (barrel.fluidInstance.getAmount() / (float) WoodenBarrelBlockEntity.CAPACITY * 14) + 1;
			buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
			boolean isLava = FluidTags.LAVA.contains(barrel.fluidInstance.getFluid());
			int color = isLava ? 16777215 : BiomeColors.waterColorAt(barrel.getWorld(), barrel.getPos());
			float r = (float) (color >> 16 & 255) / 255.0F;
			float g = (float) (color >> 8 & 255) / 255.0F;
			float b = (float) (color & 255) / 255.0F;
			//			float r = 1F, g = 1F, b = 1F;
			buffer.vertex(frac(3), frac(height), frac(3)).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1F).next();
			buffer.vertex(frac(3), frac(height), frac(13)).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1F).next();
			buffer.vertex(frac(13), frac(height), frac(13)).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1F).next();
			buffer.vertex(frac(13), frac(height), frac(3)).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1F).next();
			buffer.setOffset(0.0, 0.0, 0.0);
			tessellator.draw();
			GlStateManager.enableLighting();
		}
		super.render(barrel, x, y, z, ticksDelta, destroyStage);
	}

}
