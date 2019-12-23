package team.reborn.assembly.client.renderer.blockentityrenderer;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.assembly.blockentity.TreeTapBlockEntity;
import team.reborn.assembly.blockentity.WoodenBarrelBlockEntity;
import team.reborn.assembly.util.MathUtil;
import team.reborn.assembly.util.block.AssemblyProperties;

public class TreeTapBlockEntityRenderer extends BlockEntityRenderer<TreeTapBlockEntity> {

	public TreeTapBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TreeTapBlockEntity treeTap, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (treeTap.getWorld().getBlockState(treeTap.getPos()).get(AssemblyProperties.POURING)) {
			Direction facing = treeTap.getWorld().getBlockState(treeTap.getPos()).get(Properties.HORIZONTAL_FACING);
			Fluid fluid = treeTap.getPouringFluid();
			if (fluid != Fluids.EMPTY) {
				FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
				if (handler != null) {
					BlockPos pos = treeTap.getPos();
					World world = treeTap.getWorld();
					FluidState state = fluid.getDefaultState();
					Sprite sprite = handler.getFluidSprites(world, pos, state)[1];
					MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
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

					VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

					matrices.push();
					matrices.translate(0.5, 0.5, 0.5);
					matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90F + facing.asRotation()));
					matrices.translate(-0.5, -0.5, -0.5);

					Matrix4f modelMatrix = matrices.peek().getModel();
					Matrix3f normalMatrix = matrices.peek().getNormal();

					//Top stream;
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU((MathUtil.getXFromU(sprite, sprite.getMinU()) + 7) * flowingMultiplier), sprite.getFrameV((MathUtil.getYFromV(sprite, sprite.getMaxV()) + 0.44) * flowingMultiplier)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU((MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7) * flowingMultiplier), sprite.getFrameV((MathUtil.getYFromV(sprite, sprite.getMaxV()) + 0.44) * flowingMultiplier)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(16), MathUtil.fracf(topHeight), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU((MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7) * flowingMultiplier), sprite.getFrameV((MathUtil.getYFromV(sprite, sprite.getMinV()) + 9.96) * flowingMultiplier)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(16), MathUtil.fracf(topHeight), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU((MathUtil.getXFromU(sprite, sprite.getMinU()) + 7) * flowingMultiplier), sprite.getFrameV((MathUtil.getYFromV(sprite, sprite.getMinV()) + 9.96) * flowingMultiplier)).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					double bottomStream = 0;
					BlockEntity below = world.getBlockEntity(treeTap.getPos().down());
					if (below instanceof WoodenBarrelBlockEntity) {
						bottomStream = -16 + (((WoodenBarrelBlockEntity) below).fluidInstance.getAmount().getRawValue() / (double) WoodenBarrelBlockEntity.CAPACITY.getRawValue() * 14) + 1;
					}

					double streamHeight = bottomAngled - bottomStream;

					if (streamHeight > 16) {
						//Slight stretching will occur, but I think that's better than rendering another quad
						streamHeight = 16;
					}

					//front stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Back stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomStream), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomStream), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Left Stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7.5)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomStream), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Left angled bit
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7.5)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled + 0.58 * Math.tan(radiansAngle)), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMinV())))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMinV()) + 1))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(7.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMinV()) + 1))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Right Stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7.5)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomStream), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Right angled bit
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7.5)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMinV()) + 1))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMinV()) + 1))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 0.58), MathUtil.fracf(bottomAngled + 0.58 * Math.tan(radiansAngle)), MathUtil.fracf(9.5)).color(r, g, b, 1F).texture(sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7)), sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()))).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					matrices.pop();
				}
			}
		}
	}

}
