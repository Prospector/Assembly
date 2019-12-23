package team.reborn.assembly.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.fluid.container.GenericFluidContainer;
import team.reborn.assembly.network.AssemblyNetworking;

import javax.annotation.Nonnull;
import java.util.List;

public class WoodenBarrelBlockEntity extends BlockEntity implements GenericFluidContainer<Direction>, Tickable {
	public static final FluidValue CAPACITY = FluidValue.BUCKET;

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
		if (world != null) {
			if (world.isClient && firstTick) {
				AssemblyNetworking.requestBarrelSync(this);
				firstTick = false;
			}
			if (!world.isClient && world.getTime() % 10 == 0) {
				List<Entity> nearbyEntities = world.getEntities((Entity) null, new Box(pos.getX() - 32, pos.getY() - 32, pos.getZ() - 32, pos.getX() + 32, pos.getY() + 32, pos.getZ() + 32), entity -> entity instanceof ServerPlayerEntity);
				boolean entitiesChanged = entitiesLastSync != null && !entitiesLastSync.equals(nearbyEntities);
				if (!fluidLastSync.equals(fluidInstance) || entitiesChanged) {
					for (Entity entity : nearbyEntities) {
						AssemblyNetworking.syncBarrelFluid(this, (ServerPlayerEntity) entity);
					}
					fluidLastSync = fluidInstance.copy();
				}
			}
		}
	}

	@Nonnull
	@Override
	public FluidInstance getFluidInstance(Direction direction) {
		return fluidInstance;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put(FluidInstance.FLUID_KEY, fluidInstance.write());
		return super.toTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		fluidInstance.read(tag.getCompound(FluidInstance.FLUID_KEY));
	}

	@Override
	public FluidValue getCapacity(Direction type) {
		return CAPACITY;
	}

	@Override
	public boolean canInsertFluid(Direction fromSide, @Nonnull Fluid fluid, FluidValue amount) {
		return this.fluidInstance == null || this.fluidInstance.getFluid() == Fluids.EMPTY || fluid == this.fluidInstance.getFluid() && fluidInstance.getAmount().add(amount).lessThanOrEqual(CAPACITY);
	}

	@Override
	public boolean canExtractFluid(Direction fromSide, @Nonnull Fluid fluid, FluidValue amount) {
		return fluid == this.fluidInstance.getFluid() && amount.lessThanOrEqual(fluidInstance.getAmount());
	}

	@Override
	public void insertFluid(Direction fromSide, @Nonnull Fluid fluid, FluidValue amount) {
		if (canInsertFluid(fromSide, fluid, amount)) {
			fluidInstance.setFluid(fluid);
			fluidInstance.addAmount(amount);
		}
	}

	@Override
	public void extractFluid(Direction fromSide, @Nonnull Fluid fluid, FluidValue amount) {
		if (canExtractFluid(fromSide, fluid, amount)) {
			fluidInstance.subtractAmount(amount);
			if (fluidInstance.getAmount().isEmpty()) {
				this.fluidInstance.setFluid(Fluids.EMPTY);
			}
		}
	}

	@Override
	public void setFluid(Direction fromSide, @Nonnull FluidInstance instance) {
		this.fluidInstance = instance;
	}
}
