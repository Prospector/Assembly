package teamreborn.assembly.blockentity;

import io.github.prospector.silk.fluid.DropletValues;
import io.github.prospector.silk.fluid.FluidContainer;
import io.github.prospector.silk.fluid.FluidInstance;
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
import net.minecraft.util.math.Direction;
import teamreborn.assembly.network.AssemblyNetworking;
import teamreborn.assembly.registry.AssemblyBlockEntities;

import java.util.List;

public class WoodenBarrelBlockEntity extends BlockEntity implements FluidContainer, Tickable {
	public static final String FLUID_KEY = "Fluid";
	public static final int CAPACITY = DropletValues.BLOCK;

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
		if (world.isClient && firstTick) {
			AssemblyNetworking.requestBarrelSync(this);
			firstTick = false;
		}
		if (!world.isClient && world.getTime() % 10 == 0) {
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
	public CompoundTag toTag(CompoundTag tag) {
		tag.put(FLUID_KEY, fluidInstance.toTag(new CompoundTag()));
		return super.toTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		fluidInstance.fromTag(tag.getCompound(FLUID_KEY));
	}

	@Override
	public boolean canInsertFluid(Direction fromSide, Fluid fluid, int amount) {
		return this.fluidInstance == null || this.fluidInstance.getFluid() == Fluids.EMPTY || fluid == this.fluidInstance.getFluid() && fluidInstance.getAmount() + amount <= CAPACITY;
	}

	@Override
	public boolean canExtractFluid(Direction fromSide, Fluid fluid, int amount) {
		return fluid == this.fluidInstance.getFluid() && amount <= fluidInstance.getAmount();
	}

	@Override
	public void insertFluid(Direction fromSide, Fluid fluid, int amount) {
		if (canInsertFluid(fromSide, fluid, amount)) {
			fluidInstance.setFluid(fluid);
			fluidInstance.grow(amount);
		}
	}

	@Override
	public void extractFluid(Direction fromSide, Fluid fluid, int amount) {
		if (canExtractFluid(fromSide, fluid, amount)) {
			fluidInstance.shrink(amount);
			if (fluidInstance.getAmount() == 0) {
				this.fluidInstance.setFluid(Fluids.EMPTY);
			}
		}
	}

	@Override
	public void setFluid(Direction fromSide, FluidInstance instance) {
		this.fluidInstance = instance;
	}

	@Override
	public FluidInstance[] getFluids(Direction fromSide) {
		return new FluidInstance[] { fluidInstance };
	}
}
