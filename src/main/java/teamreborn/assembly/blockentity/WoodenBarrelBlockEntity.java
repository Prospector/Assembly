package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Facing;
import prospector.silk.fluid.FluidContainer;
import prospector.silk.fluid.FluidInstance;
import teamreborn.assembly.registry.AssemblyBlockEntities;
import teamreborn.assembly.registry.AssemblyFluids;

public class WoodenBarrelBlockEntity extends BlockEntity implements FluidContainer, Tickable {
	public static final String FLUID_KEY = "Fluid";
	public static final int CAPACITY = 1000;

	public FluidInstance fluidInstance = new FluidInstance(Fluids.EMPTY);

	public WoodenBarrelBlockEntity() {
		super(AssemblyBlockEntities.WOODEN_BARREL);
	}

	@Override
	public void tick() {
		if (world.isRemote && world.getTime() % (5 + world.getRandom().nextInt(15)) == 0) {
			if (fluidInstance.getFluid() == Fluids.EMPTY) {
				fluidInstance.setFluid(AssemblyFluids.LATEX);
			}
			if (fluidInstance.getAmount() < CAPACITY)
				fluidInstance.grow(1);
		}
	}

	@Override
	public CompoundTag serialize(CompoundTag tag) {
		tag.put(FLUID_KEY, fluidInstance.serialize(new CompoundTag()));
		return super.serialize(tag);
	}

	@Override
	public void deserialize(CompoundTag tag) {
		super.deserialize(tag);
		fluidInstance.deserialize(tag.getCompound(FLUID_KEY));
	}

	@Override
	public boolean canInsertFluid(Facing fromSide, Fluid fluid, int amount) {
		return this.fluidInstance == null || this.fluidInstance.getFluid() == Fluids.EMPTY || fluid == this.fluidInstance.getFluid() && fluidInstance.getAmount() + amount <= CAPACITY;
	}

	@Override
	public boolean canExtractFluid(Facing fromSide, Fluid fluid, int amount) {
		return fluid == this.fluidInstance.getFluid() && amount <= fluidInstance.getAmount();
	}

	@Override
	public void insertFluid(Facing fromSide, Fluid fluid, int amount) {
		if (canInsertFluid(fromSide, fluid, amount)) {
			fluidInstance.setFluid(fluid);
			fluidInstance.grow(amount);
		}
	}

	@Override
	public void extractFluid(Facing fromSide, Fluid fluid, int amount) {
		if (canExtractFluid(fromSide, fluid, amount)) {
			fluidInstance.shrink(amount);
			if (fluidInstance.getAmount() == 0) {
				this.fluidInstance.setFluid(Fluids.EMPTY);
			}
		}
	}

	@Override
	public void setFluid(Facing fromSide, FluidInstance instance) {
		this.fluidInstance = instance;
	}

	@Override
	public FluidInstance[] getFluids(Facing fromSide) {
		return new FluidInstance[] { fluidInstance };
	}
}
