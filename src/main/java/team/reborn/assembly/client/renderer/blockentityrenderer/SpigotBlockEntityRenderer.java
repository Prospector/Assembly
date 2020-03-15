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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import team.reborn.assembly.block.SpigotBlock;
import team.reborn.assembly.blockentity.FluidHopperBlockEntity;
import team.reborn.assembly.blockentity.SpigotBlockEntity;
import team.reborn.assembly.blockentity.FluidBarrelBlockEntity;
import team.reborn.assembly.util.math.MathUtil;

public class SpigotBlockEntityRenderer extends BlockEntityRenderer<SpigotBlockEntity> {

	public SpigotBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(SpigotBlockEntity spigot, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = spigot.getWorld();
		if (world != null && world.getBlockState(spigot.getPos()).get(SpigotBlock.POURING)) {
			Direction facing = world.getBlockState(spigot.getPos()).get(SpigotBlock.FACING);
			Fluid fluid = spigot.getPouringFluid();
			if (fluid != Fluids.EMPTY) {
				FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
				if (handler != null) {
					BlockPos pos = spigot.getPos();
					FluidState state = fluid.getDefaultState();
					Sprite sprite = handler.getFluidSprites(world, pos, state)[1];
					MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
					int color = handler.getFluidColor(world, pos, state);
					float r = (float) (color >> 16 & 255) / 255.0F;
					float g = (float) (color >> 8 & 255) / 255.0F;
					float b = (float) (color & 255) / 255.0F;
					double top = 5;
					double endOfFlow = 16 - 8;
					double flowingMultiplier = 0.5;

					VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

					matrices.push();
					matrices.translate(0.5, 0.5, 0.5);
					matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90F + facing.asRotation()));
					matrices.translate(-0.5, -0.5, -0.5);

					Matrix4f modelMatrix = matrices.peek().getModel();
					Matrix3f normalMatrix = matrices.peek().getNormal();

					double bottomStream = 0;
					BlockEntity below = world.getBlockEntity(spigot.getPos().down());
					if (below instanceof FluidBarrelBlockEntity) {
						bottomStream = -16 + (((FluidBarrelBlockEntity) below).getTank().getInvFluid(0).getAmount_F().asInexactDouble() / (double) ((FluidBarrelBlockEntity) below).getTank().getCapacity(0).asInexactDouble() * 14) + 1;
					} else if (below instanceof FluidHopperBlockEntity) {
						bottomStream = -5;
					}

					double streamHeight = top - bottomStream;

					if (streamHeight > 16) {
						//Slight stretching will occur, but I think that's better than rendering another quad
						streamHeight = 16;
					}

					//front stream
					float uMaxP7 = sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMinU()) + 7));
					float uMaxM7 = sprite.getFrameU(flowingMultiplier * (MathUtil.getXFromU(sprite, sprite.getMaxU()) - 7));
					float vMin = sprite.getFrameV(flowingMultiplier * MathUtil.getYFromV(sprite, sprite.getMinV()));
					float vMax = sprite.getFrameV(flowingMultiplier * (MathUtil.getYFromV(sprite, sprite.getMaxV()) - (16 - streamHeight)));
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(top), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxP7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxP7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxM7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(top), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxM7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Back stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(top), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxP7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(top), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxM7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(bottomStream), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxM7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(bottomStream), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxP7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Left Stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(top), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxP7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(top), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxM7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(bottomStream), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxM7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(7)).color(r, g, b, 1F).texture(uMaxP7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					//Right Stream
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(top), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxP7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow), MathUtil.fracf(bottomStream), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxP7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(bottomStream), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxM7, vMax).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();
					buffer.vertex(modelMatrix, MathUtil.fracf(endOfFlow + 2), MathUtil.fracf(top), MathUtil.fracf(9)).color(r, g, b, 1F).texture(uMaxM7, vMin).light(light).normal(normalMatrix, 0.0F, 1.0F, 0.0F).next();

					matrices.pop();
				}
			}
		}
	}

}
