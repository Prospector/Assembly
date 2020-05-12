package com.terraformersmc.assembly.client.renderer.blockentityrenderer;

import com.terraformersmc.assembly.block.SteamPressBlock;
import com.terraformersmc.assembly.blockentity.SteamPressBlockEntity;
import com.terraformersmc.assembly.client.AssemblyModels;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class SteamPressBlockEntityRenderer extends BlockEntityRenderer<SteamPressBlockEntity> {

	public SteamPressBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(SteamPressBlockEntity steamPress, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = steamPress.getWorld();
		BlockPos pos = steamPress.getPos();
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
				MinecraftClient client = MinecraftClient.getInstance();
				Direction facing = state.get(SteamPressBlock.FACING);

				ItemStack stack = steamPress.getRenderStack();
				if (!stack.isEmpty()) {
					// Item Rendering
					matrixStack.push();
					matrixStack.translate(0.5, -5 / 16F, 0.5);
					matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(180F + facing.asRotation()));
					matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
					matrixStack.scale(0.6F, 0.6F, 0.6F);
					client.getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrixStack, vertexConsumers);
					matrixStack.pop();
				}

				// Arm Rendering
				matrixStack.push();
				BakedModel armModel = client.getBakedModelManager().getModel(AssemblyModels.STEAM_PRESS_UPPER_ARM);
				matrixStack.translate(0, steamPress.getArmOffset(), 0);
				client.getBlockRenderManager().getModelRenderer().render(world, armModel, state, pos, matrixStack, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, new Random(), state.getRenderingSeed(pos), overlay);
				matrixStack.pop();
			}
		}
	}
}
