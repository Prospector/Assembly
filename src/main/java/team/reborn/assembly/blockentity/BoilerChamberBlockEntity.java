package team.reborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.fluid.container.GenericFluidContainer;

import javax.annotation.Nonnull;

public class BoilerChamberBlockEntity extends BlockEntity implements GenericFluidContainer<Direction> {

	private BoilerBlockEntity boiler;
	private BlockPos boilerPos;

	public BoilerChamberBlockEntity() {
		super(AssemblyBlockEntities.BOILER_CHAMBER);
	}

	public void updateBoiler(BlockPos pos) {
		if (world == null || pos == null) {
			return;
		}
		boilerPos = pos;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BoilerBlockEntity) {
			boiler = (BoilerBlockEntity) blockEntity;
		} else {
			boiler = null;
		}
	}

	@Override
	public boolean canInsertFluid(Direction type, @Nonnull Fluid fluid, FluidValue amount) {
		return false;
	}

	@Override
	public void setFluid(Direction direction, @Nonnull FluidInstance instance) {
	}

	@Nonnull
	@Override
	public FluidInstance getFluidInstance(Direction type) {
		updateBoiler(boilerPos);
		if (boiler == null) {
			return FluidInstance.EMPTY;
		}
		return boiler.outputTank.getFluidInstance(type);
	}

	@Override
	public FluidValue getCapacity(Direction type) {
		updateBoiler(boilerPos);
		if (boiler == null) {
			return FluidValue.EMPTY;
		}
		return boiler.outputTank.getCapacity();
	}
}
