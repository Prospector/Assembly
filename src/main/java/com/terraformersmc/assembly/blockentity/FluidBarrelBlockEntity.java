package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtBlockEntity;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.interaction.interactable.TankIOInteractable;

public class FluidBarrelBlockEntity extends AssemblySyncedNbtBlockEntity implements Tickable, TankIOInteractable {

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount CAPACITY = FluidAmount.BUCKET;
	private final IOFluidContainer tank;

	private FluidVolume fluidLastSync = FluidVolumeUtil.EMPTY;

	public FluidBarrelBlockEntity() {
		super(AssemblyBlockEntities.FLUID_BARREL);
		tank = new SimpleIOFluidContainer(1, CAPACITY);
	}

	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			if (!fluidLastSync.equals(tank.getInvFluid(0))) {
				sync();
				fluidLastSync = tank.getInvFluid(0).copy();
			}
		}
	}

	public IOFluidContainer getTank() {
		return tank;
	}

	@Override
	public void fromTag(CompoundTag tag, boolean syncing) {
		if (tag.contains(FLUIDS_KEY)) {
			FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
			this.tank.setInvFluid(0, fluid, Simulation.ACTION);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag, boolean syncing) {
		FluidVolume inputFluid = this.tank.getInvFluid(0);
		tag.put(FLUIDS_KEY, inputFluid.toTag());
		return tag;
	}

	@Override
	public FluidInsertable getInteractableInsertable() {
		return getTank();
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		return getTank();
	}
}
