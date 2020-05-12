package com.terraformersmc.assembly.client.renderer.blockentityrenderer;

import com.terraformersmc.assembly.block.TinkeringTableBlock;
import com.terraformersmc.assembly.blockentity.TinkeringTableBlockEntity;
import net.minecraft.block.BlockState;
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

public class TinkeringTableBlockEntityRenderer extends BlockEntityRenderer<TinkeringTableBlockEntity> {

	public TinkeringTableBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TinkeringTableBlockEntity tinkeringTable, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = tinkeringTable.getWorld();
		BlockPos pos = tinkeringTable.getPos();
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof TinkeringTableBlock) {
				ItemStack stack = tinkeringTable.getRenderingStack();
				if (!stack.isEmpty()) {
					MinecraftClient client = MinecraftClient.getInstance();
					Direction facing = state.get(TinkeringTableBlock.FACING);

					// Item Rendering
					matrixStack.push();
					matrixStack.translate(0.5, 13 / 16F, 0.5);
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
