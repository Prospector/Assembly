package com.terraformersmc.assembly.client.renderer.blockentityrenderer;

import com.terraformersmc.assembly.block.FluidInjectorBlock;
import com.terraformersmc.assembly.block.SteamPressBlock;
import com.terraformersmc.assembly.blockentity.FluidInjectorBlockEntity;
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

public class FluidInjectorBlockEntityRenderer extends BlockEntityRenderer<FluidInjectorBlockEntity> {

	public FluidInjectorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(FluidInjectorBlockEntity fluidInjector, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = fluidInjector.getWorld();
		BlockPos pos = fluidInjector.getPos();
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof FluidInjectorBlock) {
				MinecraftClient client = MinecraftClient.getInstance();
				Direction facing = state.get(FluidInjectorBlock.FACING);

				// Item Rendering
				matrices.push();
				matrices.translate(0.5, 11 / 16F, 0.5);
				matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(180F + facing.asRotation()));
				matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
				matrices.scale(0.6F, 0.6F, 0.6F);
				ItemStack stack = fluidInjector.getRenderStack();
				client.getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers);
				matrices.pop();
			}
		}
	}
}
