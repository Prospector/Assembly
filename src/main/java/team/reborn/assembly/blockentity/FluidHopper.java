package team.reborn.assembly.blockentity;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import team.reborn.assembly.util.fluid.IOFluidContainer;

import javax.annotation.Nullable;

public interface FluidHopper {
	VoxelShape INSIDE_SHAPE = Block.createCuboidShape(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	VoxelShape ABOVE_SHAPE = Block.createCuboidShape(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
	VoxelShape INPUT_AREA_SHAPE = VoxelShapes.union(INSIDE_SHAPE, ABOVE_SHAPE);

	IOFluidContainer getFluidTank();

	default VoxelShape getInputAreaShape() {
		return INPUT_AREA_SHAPE;
	}

	@Nullable
	World getWorld();

	double getHopperX();

	double getHopperY();

	double getHopperZ();
}
