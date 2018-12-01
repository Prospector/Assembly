package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Facing;
import net.minecraft.util.registry.Registry;
import prospector.silk.blockentity.FluidContainer;
import teamreborn.assembly.registry.AssemblyBlockEntities;

public class WoodenBarrelBlockEntity extends BlockEntity implements FluidContainer, Tickable {
	public static final int CAPACITY = 1000;

	public Fluid currentFluid = Fluids.EMPTY;
	public int fluidAmount;

	public WoodenBarrelBlockEntity() {
		super(AssemblyBlockEntities.WOODEN_BARREL);
	}

	@Override
	public void tick() {
		if (world.isRemote && world.getTime() % (5 + world.getRandom().nextInt(15)) == 0) {
			if (currentFluid == Fluids.EMPTY) {
				currentFluid = Fluids.WATER;
			}
			fluidAmount++;
		}
	}

	@Override
	public CompoundTag serialize(CompoundTag tag) {
		tag.putString("CurrentFluid", Registry.FLUIDS.getId(currentFluid).toString());
		tag.putInt("FluidAmount", fluidAmount);
		return super.serialize(tag);
	}

	@Override
	public void deserialize(CompoundTag tag) {
		super.deserialize(tag);
		currentFluid = Registry.FLUIDS.get(new Identifier(tag.getString("CurrentFluid")));
		fluidAmount = tag.getInt("FluidAmount");
	}

	@Override
	public boolean canInsertFluid(Facing fromSide, Fluid fluid, int amount) {
		return currentFluid == null || currentFluid == Fluids.EMPTY || fluid.equals(currentFluid) && fluidAmount + amount <= CAPACITY;
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
				currentFluid = Fluids.EMPTY;
			}
		}
	}
}
