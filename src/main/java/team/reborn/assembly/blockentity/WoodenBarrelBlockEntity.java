package team.reborn.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import team.reborn.assembly.attributes.IOFluidContainer;
import team.reborn.assembly.attributes.SimpleIOFluidContainer;
import team.reborn.assembly.util.AssemblyConstants;

public class WoodenBarrelBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount CAPACITY = FluidAmount.BUCKET;
	private final IOFluidContainer tank;

	private FluidVolume fluidLastSync = FluidVolumeUtil.EMPTY;

	public WoodenBarrelBlockEntity() {
		super(AssemblyBlockEntities.WOODEN_BARREL);
		tank = new SimpleIOFluidContainer(1, CAPACITY);
	}

	@Override
	public void tick() {
		if (world != null) {
			if (!world.isClient && world.getTime() % 10 == 0) {
				if (!fluidLastSync.equals(tank.getInvFluid(0))) {
					sync();
					fluidLastSync = tank.getInvFluid(0).copy();
				}
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.contains(FLUIDS_KEY)) {
			FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
			this.tank.setInvFluid(0, fluid, Simulation.ACTION);
		}
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		FluidVolume inputFluid = this.tank.getInvFluid(0);
		if (!inputFluid.isEmpty()) {
			tag.put(FLUIDS_KEY, inputFluid.toTag());
		}
		return tag;
	}

	public IOFluidContainer getTank() {
		return tank;
	}

	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}
}
