package teamreborn.assembly.block;

import io.github.prospector.silk.block.SilkBlockWithEntity;
import io.github.prospector.silk.fluid.DropletValues;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class WoodenBarrelBlock extends SilkBlockWithEntity {
	protected static final VoxelShape BOUNDING_SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public WoodenBarrelBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new WoodenBarrelBlockEntity();
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof WoodenBarrelBlockEntity) {
			WoodenBarrelBlockEntity barrel = (WoodenBarrelBlockEntity) blockEntity;
			Fluid fluid = barrel.fluidInstance.getFluid();
			if (!player.isSneaking() && player.getStackInHand(hand).getItem().equals(Items.BUCKET) && barrel.canExtractFluid(hitResult.getSide(), fluid, DropletValues.BUCKET)) {
				barrel.extractFluid(hitResult.getSide(), fluid, DropletValues.BUCKET);
				if (!player.abilities.creativeMode) {
					player.setStackInHand(hand, fluid.getBucketItem().getDefaultStack());
				}
			}
		}
		return super.activate(state, world, pos, player, hand, hitResult);
	}
}
