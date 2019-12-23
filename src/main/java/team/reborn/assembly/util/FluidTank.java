package team.reborn.assembly.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.client.containerBuilder.builder.Syncable;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.fluid.container.GenericFluidContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidTank implements GenericFluidContainer<Direction>, Syncable {

	private final String name;
	@Nonnull
	private FluidInstance fluidInstance = new FluidInstance();
	private FluidValue capacity;

	@Nullable
	private Direction side = null;

	private final BlockEntity blockEntity;

	public FluidTank(String name, FluidValue capacity, BlockEntity blockEntity) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.blockEntity = blockEntity;
	}

	@Nonnull
	public FluidInstance getFluidInstance() {
		return getFluidInstance(side);
	}

	@Nonnull
	public Fluid getFluid() {
		return getFluidInstance().getFluid();
	}

	public void setCapacity(FluidValue capacity) {
		this.capacity = capacity;
		if (!fluidInstance.isEmpty() && fluidInstance.getAmount().moreThan(capacity)) {
			fluidInstance.setAmount(capacity);
		}
	}

	public FluidValue getCapacity() {
		return capacity;
	}

	public FluidValue getFreeSpace() {
		return getCapacity().subtract(getFluidAmount());
	}

	public boolean canFit(Fluid fluid, FluidValue amount) {
		return (getFluid() == Fluids.EMPTY || getFluid() == fluid) && getFreeSpace().moreThan(amount);
	}

	public boolean isEmpty() {
		return getFluidInstance().isEmpty();
	}

	public boolean isFull() {
		return !getFluidInstance().isEmpty() && getFluidInstance().getAmount().equalOrMoreThan(getCapacity());
	}

	public final CompoundTag write(CompoundTag nbt) {
		CompoundTag tankData = fluidInstance.write();
		nbt.put(name, tankData);
		return nbt;
	}

	public void setFluidAmount(FluidValue amount) {
		if (!fluidInstance.isEmpty()) {
			fluidInstance.setAmount(amount);
		}
	}

	public final FluidTank read(CompoundTag nbt) {
		if (nbt.contains(name)) {
			// allow to read empty tanks
			setFluid(Fluids.EMPTY);

			CompoundTag tankData = nbt.getCompound(name);
			fluidInstance = new FluidInstance(tankData);
		}
		return this;
	}

	public void setFluid(@Nonnull Fluid f) {
		Validate.notNull(f);
		fluidInstance.setFluid(f);
	}

	@Nullable
	public Direction getSide() {
		return side;
	}

	public void setSide(
		@Nullable
			Direction side) {
		this.side = side;
	}

	@Override
	public void getSyncPair(List<Pair<Supplier, Consumer>> pairList) {
		pairList.add(Pair.of(() -> fluidInstance.getAmount(), o -> fluidInstance.setAmount((FluidValue) o)));
		pairList.add(Pair.of(() -> Registry.FLUID.getId(fluidInstance.getFluid()).toString(), (Consumer<String>) o -> fluidInstance.setFluid(Registry.FLUID.get(new Identifier(o)))));
	}

	public FluidValue getFluidAmount() {
		return getFluidInstance().getAmount();
	}

	@Override
	public void setFluid(@Nullable Direction type, @Nonnull FluidInstance instance) {
		fluidInstance = instance;
	}

	@Nonnull
	@Override
	public FluidInstance getFluidInstance(@Nullable Direction type) {
		return fluidInstance;
	}

	@Override
	public FluidValue getCapacity(@Nullable Direction type) {
		return capacity;
	}


}
