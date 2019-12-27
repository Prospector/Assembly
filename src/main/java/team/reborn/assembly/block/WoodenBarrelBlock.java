package team.reborn.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import team.reborn.assembly.attributes.IOFluidContainer;
import team.reborn.assembly.blockentity.WoodenBarrelBlockEntity;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.item.AssemblyItems;

public class WoodenBarrelBlock extends Block implements BlockEntityProvider, AttributeProvider {
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public WoodenBarrelBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof WoodenBarrelBlockEntity) {
			WoodenBarrelBlockEntity boiler = (WoodenBarrelBlockEntity) be;
			to.offer(boiler.getTank(), VoxelShapes.fullCube());
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public boolean hasBlockEntity() {
		return true;
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
			IOFluidContainer tank = barrel.getTank();
			FluidVolume volume = tank.getInvFluid(0);
			Fluid fluid = volume.getRawFluid();
			if (fluid != null && fluid != Fluids.EMPTY) {
				if (!player.isSneaking()) {
					FluidVolume simulated = tank.attemptAnyExtraction(FluidAmount.BUCKET, Simulation.SIMULATE);
					if (player.getStackInHand(hand).getItem().equals(Items.BUCKET) && simulated.getAmount_F().equals(FluidAmount.BUCKET)) {
						tank.attemptAnyExtraction(FluidAmount.BUCKET, Simulation.SIMULATE);
						if (!player.abilities.creativeMode) {
							player.setStackInHand(hand, new ItemStack(fluid.getBucketItem()));
						}
						return ActionResult.SUCCESS;
					}
					if (player.getStackInHand(hand).getItem().equals(AssemblyItems.FORMIC_ACID) && fluid == AssemblyFluids.LATEX && !volume.getAmount_F().isLessThan(FluidAmount.BUCKET.roundedDiv(5))) {
						int count = volume.getAmount_F().bigDiv(FluidAmount.BUCKET.roundedDiv(5)).asLongIntRounded().asInt(1);
						System.out.println(count);
						tank.extract(FluidAmount.BUCKET.roundedDiv(5).roundedMul(count));
						if (!player.isCreative()) {
							player.getStackInHand(hand).decrement(1);
						}
						player.giveItemStack(new ItemStack(AssemblyItems.COAGULATED_LATEX, count));
						return ActionResult.SUCCESS;
					}
				}
			}
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}
}
