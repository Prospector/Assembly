package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.Facing;
import teamreborn.assembly.registry.AssemblyBlockEntities;
import prospector.silk.blockentity.FluidContainer;

public class SappingBarrelBlockEntity extends BlockEntity implements FluidContainer {
	public static final int CAPACITY = 1000;

	public Fluid currentFluid;
	public int fluidAmount;

	public SappingBarrelBlockEntity() {
		super(AssemblyBlockEntities.SAPPING_BARREL_BLOCK_ENTITY);
	}

	@Override
	public boolean canInsertFluid(Facing fromSide, Fluid fluid, int amount) {
		return currentFluid == null || fluid.equals(currentFluid) && fluidAmount + amount <= CAPACITY;
	}

	@Override
	public boolean canExtractFluid(Facing fromSide, Fluid fluid, int amount) {
		return fluid.equals(currentFluid) && amount <= fluidAmount;
	}

	@Override
	public void insertFluid(Facing fromSide, Fluid fluid, int amount) {
		if (canInsertFluid(fromSide, fluid, amount)) {
			currentFluid = fluid;
			fluidAmount += amount;
		}
	}

	@Override
	public void extractFluid(Facing fromSide, Fluid fluid, int amount) {
		if (canExtractFluid(fromSide, fluid, amount)) {
			fluidAmount -= amount;
			if (fluidAmount == 0) {
				currentFluid = null;
			}
		}
	}
}
