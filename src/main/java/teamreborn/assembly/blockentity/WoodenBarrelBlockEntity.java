package teamreborn.assembly.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Facing;
import prospector.silk.fluid.FluidContainer;
import prospector.silk.fluid.FluidInstance;
import prospector.silk.fluid.PlopValues;
import teamreborn.assembly.network.AssemblyNetworking;
import teamreborn.assembly.registry.AssemblyBlockEntities;

import java.util.List;

public class WoodenBarrelBlockEntity extends BlockEntity implements FluidContainer, Tickable {
	public static final String FLUID_KEY = "Fluid";
	public static final int CAPACITY = PlopValues.BLOCK;

	public FluidInstance fluidInstance = new FluidInstance(Fluids.EMPTY);

	@Environment(EnvType.SERVER)
	public FluidInstance fluidLastSync = new FluidInstance(Fluids.EMPTY);
	@Environment(EnvType.SERVER)
	public List<Entity> entitiesLastSync = null;

	@Environment(EnvType.CLIENT)
	boolean firstTick = true;

	public WoodenBarrelBlockEntity() {
		super(AssemblyBlockEntities.WOODEN_BARREL);
	}

	@Override
	public void tick() {
		if (world.isRemote && firstTick) {
			AssemblyNetworking.requestBarrelSync(this);
			firstTick = false;
		}
		if (!world.isRemote && world.getTime() % 10 == 0) {
			List<Entity> nearbyEntities = world.getEntities((Entity) null, new BoundingBox(pos.getX() - 32, pos.getY() - 32, pos.getZ() - 32, pos.getX() + 32, pos.getY() + 32, pos.getZ() + 32), entity -> entity instanceof ServerPlayerEntity);
			boolean entitiesChanged = entitiesLastSync != null && !entitiesLastSync.equals(nearbyEntities);
			if (!fluidLastSync.equals(fluidInstance) || entitiesChanged) {
				for (Entity entity : nearbyEntities) {
					AssemblyNetworking.syncBarrelFluid(this, (ServerPlayerEntity) entity);
				}
				fluidLastSync = fluidInstance.copy();
			}
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
