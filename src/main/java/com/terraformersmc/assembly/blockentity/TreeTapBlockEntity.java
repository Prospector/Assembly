package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import com.terraformersmc.assembly.block.TreeTapBlock;
import com.terraformersmc.assembly.fluid.AssemblyFluids;

public class TreeTapBlockEntity extends BlockEntity implements Tickable {
	private Fluid pouringFluid = Fluids.EMPTY;

	public TreeTapBlockEntity() {
		super(AssemblyBlockEntities.TREE_TAP);
	}

	@Override
	public void tick() {
		pouringFluid = AssemblyFluids.LATEX.getStill();
		if (world != null && !world.isClient) {
			if (world.getTime() % (20 + world.getRandom().nextInt(12)) == 0) {
				BlockPos downPos = pos.offset(Direction.DOWN);
				boolean pouring = world.getBlockState(downPos).getBlock() instanceof AttributeProvider;
				if (pouring) {
					FluidInsertable insertable = FluidAttributes.INSERTABLE.get(world, downPos);
					FluidVolume excess = insertable.attemptInsertion(FluidKeys.get(pouringFluid).withAmount(FluidAmount.BUCKET.roundedDiv(1000)), Simulation.ACTION);
					pouring = excess.isEmpty();
				}
				BlockState state = world.getBlockState(pos);
				if (state.getBlock() instanceof TreeTapBlock && state.get(TreeTapBlock.POURING) != pouring) {
					world.setBlockState(pos, state.with(TreeTapBlock.POURING, pouring));
				}
			}
		}
	}

	public Fluid getPouringFluid() {
		return pouringFluid;
	}
}
