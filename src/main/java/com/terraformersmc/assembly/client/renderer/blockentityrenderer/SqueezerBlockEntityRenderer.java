package com.terraformersmc.assembly.client.renderer.blockentityrenderer;

import com.terraformersmc.assembly.block.PressBlock;
import com.terraformersmc.assembly.blockentity.PressBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SqueezerBlockEntityRenderer extends BlockEntityRenderer<PressBlockEntity> {

	public SqueezerBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(PressBlockEntity steamPress, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = steamPress.getWorld();
		BlockPos pos = steamPress.getPos();
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof PressBlock && state.get(PressBlock.HALF) == DoubleBlockHalf.UPPER) {
				MinecraftClient client = MinecraftClient.getInstance();
				Direction facing = state.get(PressBlock.FACING);

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
			}
		}
	}
}
