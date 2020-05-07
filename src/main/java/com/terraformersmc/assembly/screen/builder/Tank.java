package com.terraformersmc.assembly.screen.builder;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Tank {
	private final int id, x, y;
	private final TankStyle style;
	final IOFluidContainer fluidContainer;
	final Supplier<FluidAmount> capacityGetter;
	final Consumer<FluidAmount> capacitySetter;
	final Supplier<FluidVolume> volumeGetter;
	final Consumer<FluidVolume> volumeSetter;
	final FluidFilter insertFilter, extractFilter;

	public Tank(int id, int x, int y, TankStyle style, IOFluidContainer fluidContainer, int slot, FluidFilter insertFilter, FluidFilter extractFilter) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.style = style;
		this.fluidContainer = fluidContainer;
		this.capacityGetter = () -> fluidContainer.getCapacity(slot);
		this.capacitySetter = fluidContainer::setCapacity;
		this.volumeGetter = () -> fluidContainer.getInvFluid(slot);
		this.volumeSetter = volume -> fluidContainer.forceSetInvFluid(slot, volume);
		this.insertFilter = insertFilter;
		this.extractFilter = extractFilter;
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public TankStyle getStyle() {
		return style;
	}

	public FluidVolume getVolume() {
		return volumeGetter.get();
	}

	public FluidAmount getCapacity() {
		return capacityGetter.get();
	}

	public FluidFilter getInsertFilter() {
		return insertFilter;
	}

	public FluidFilter getExtractFilter() {
		return extractFilter;
	}
}
