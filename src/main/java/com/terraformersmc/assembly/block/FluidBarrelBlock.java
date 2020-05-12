package com.terraformersmc.assembly.block;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.blockentity.AssemblyBlockEntities;
import com.terraformersmc.assembly.blockentity.FluidBarrelBlockEntity;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.AssemblyItems;
import com.terraformersmc.assembly.util.ComparatorUtil;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.interaction.InteractionActionResult;
import com.terraformersmc.assembly.util.interaction.Interactions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FluidBarrelBlock extends Block implements BlockEntityProvider, AttributeProvider {
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public FluidBarrelBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FluidBarrelBlockEntity) {
			to.offer(((FluidBarrelBlockEntity) blockEntity).getTank(), VoxelShapes.fullCube());
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AssemblyBlockEntities.FLUID_BARREL.instantiate();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ActionResult result = Interactions.handleDefaultInteractions(state, world, pos, player, hand, hit, (state1, world1, pos1, player1, hand1, hit1) -> {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof FluidBarrelBlockEntity) {
				FluidBarrelBlockEntity barrel = (FluidBarrelBlockEntity) blockEntity;
				IOFluidContainer tank = barrel.getTank();
				FluidVolume volume = tank.getInvFluid(0);
				Fluid fluid = volume.getRawFluid();
				if (fluid != null && fluid != Fluids.EMPTY && player.getStackInHand(hand).getItem().equals(AssemblyItems.FORMIC_ACID) && fluid == AssemblyFluids.LATEX && !volume.getAmount_F().isLessThan(FluidAmount.BUCKET.roundedDiv(5))) {
					int count = volume.getAmount_F().bigDiv(FluidAmount.BUCKET.roundedDiv(5)).asLongIntRounded().asInt(1);
					tank.extract(FluidAmount.BUCKET.roundedDiv(5).roundedMul(count));
					if (!player.isCreative()) {
						player.getStackInHand(hand).decrement(1);
					}
					player.giveItemStack(new ItemStack(AssemblyItems.COAGULATED_LATEX, count));
					return InteractionActionResult.SUCCESS;
				}
			}
			return InteractionActionResult.PASS;
		});
		return result;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof FluidBarrelBlockEntity) {
			return ComparatorUtil.calculateFluidContainerOutput(((FluidBarrelBlockEntity) blockEntity).getTank());
		}
		return 0;
	}
}
