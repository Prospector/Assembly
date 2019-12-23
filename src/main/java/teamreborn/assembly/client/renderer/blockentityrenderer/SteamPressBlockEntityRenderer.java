package teamreborn.assembly.client.renderer.blockentityrenderer;

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
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import teamreborn.assembly.block.SteamPressBlock;
import teamreborn.assembly.blockentity.SteamPressBlockEntity;
import teamreborn.assembly.client.AssemblyModels;

import java.util.Random;

public class SteamPressBlockEntityRenderer extends BlockEntityRenderer<SteamPressBlockEntity> {

	public SteamPressBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(SteamPressBlockEntity barrel, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		World world = barrel.getWorld();
		BlockPos pos = barrel.getPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
			MinecraftClient client = MinecraftClient.getInstance();
			Direction facing = state.get(SteamPressBlock.FACING);

			// Item Rendering
			matrices.push();
			matrices.translate(0.5, -5 / 16F, 0.5);
			matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(180F + facing.asRotation()));
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90F));
			matrices.scale(0.6F, 0.6F, 0.6F);
			client.getItemRenderer().renderItem(Items.GOLD_INGOT.getStackForRender(), ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers);
			matrices.pop();

			// Arm Rendering
			BakedModel armModel = client.getBakedModelManager().getModel(AssemblyModels.STEAM_PRESS_UPPER_ARM);
			int speed = 75;
			long time = world.getTime() % speed;
			float lowestPosition = 9 / 16F; //9/16 because the lowest the arm should go is 9 pixels down.
			lowestPosition = lowestPosition / 2; //Divide by 2 because the sine wave will go 9/16 above AND below when the difference between the min and the max should be 9/16.
			matrices.translate(0, lowestPosition * Math.sin(2 * Math.PI / speed * (time)) - lowestPosition, 0);
			client.getBlockRenderManager().getModelRenderer().render(world, armModel, state, pos, matrices, vertexConsumers.getBuffer(RenderLayer.getSolid()), false, new Random(), state.getRenderingSeed(pos), overlay);
		}
	}
}
