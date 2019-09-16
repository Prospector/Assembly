package teamreborn.assembly.block;

import io.github.prospector.silk.fluid.DropletValues;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import teamreborn.assembly.block.base.AssemblyBlockWithEntity;
import teamreborn.assembly.blockentity.WoodenBarrelBlockEntity;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.item.AssemblyItems;

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
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof WoodenBarrelBlockEntity) {
			WoodenBarrelBlockEntity barrel = (WoodenBarrelBlockEntity) blockEntity;
			Fluid fluid = barrel.fluidInstance.getFluid();
			if (!player.isSneaking()) {
				if (player.getStackInHand(hand).getItem().equals(Items.BUCKET) && barrel.canExtractFluid(hitResult.getSide(), fluid, DropletValues.BUCKET)) {
					barrel.extractFluid(hitResult.getSide(), fluid, DropletValues.BUCKET);
					if (!player.abilities.creativeMode) {
						player.setStackInHand(hand, fluid.getBucketItem().getStackForRender());
					}
					return true;
				}
				if (player.getStackInHand(hand).getItem().equals(AssemblyItems.FORMIC_ACID) && fluid == AssemblyFluids.LATEX && barrel.fluidInstance.getAmount() >= DropletValues.BUCKET / 6) {
					int count = barrel.fluidInstance.getAmount() / (DropletValues.BUCKET / 6);
					barrel.extractFluid(hitResult.getSide(), fluid, DropletValues.BUCKET / 6 * count);
					player.giveItemStack(new ItemStack(AssemblyItems.CRUDE_RUBBER, count));
					return true;
				}
			}
		}
		return super.activate(state, world, pos, player, hand, hitResult);
	}
}
