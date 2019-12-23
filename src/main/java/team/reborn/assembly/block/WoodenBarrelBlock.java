package team.reborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import reborncore.common.fluid.FluidValue;
import team.reborn.assembly.block.base.AssemblyBlockWithEntity;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.blockentity.WoodenBarrelBlockEntity;
import team.reborn.assembly.item.AssemblyItems;
import team.reborn.assembly.util.FluidValues;

public class WoodenBarrelBlock extends AssemblyBlockWithEntity {
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof WoodenBarrelBlockEntity) {
			WoodenBarrelBlockEntity barrel = (WoodenBarrelBlockEntity) blockEntity;
			Fluid fluid = barrel.fluidInstance.getFluid();
			if (!player.isSneaking()) {
				if (player.getStackInHand(hand).getItem().equals(Items.BUCKET) && barrel.canExtractFluid(hit.getSide(), fluid, FluidValue.BUCKET)) {
					barrel.extractFluid(hit.getSide(), fluid, FluidValue.BUCKET);
					if (!player.abilities.creativeMode) {
						player.setStackInHand(hand, fluid.getBucketItem().getStackForRender());
					}
					return ActionResult.SUCCESS;
				}
				if (player.getStackInHand(hand).getItem().equals(AssemblyItems.FORMIC_ACID) && fluid == AssemblyFluids.LATEX && barrel.fluidInstance.getAmount().equalOrMoreThan(FluidValues.FIFTH_BUCKET)) {
					int count = barrel.fluidInstance.getAmount().getRawValue() / (FluidValues.FIFTH_BUCKET.getRawValue());
					barrel.extractFluid(hit.getSide(), fluid, FluidValues.FIFTH_BUCKET.multiply(count));
					if (player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
					player.giveItemStack(new ItemStack(AssemblyItems.COAGULATED_LATEX, count));
					return ActionResult.SUCCESS;
				}
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}
}
