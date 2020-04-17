package com.terraformersmc.assembly.util.fluid;

import alexiil.mc.lib.attributes.ListenerRemovalToken;
import alexiil.mc.lib.attributes.ListenerToken;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInvTankChangeListener;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.GroupedFluidInvFixedWrapper;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.Saveable;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;
import java.util.function.Supplier;

public class BaseFluidContainer implements AssemblyFluidContainer, Saveable {

	private static final String TANKS_KEY = "Tanks";

	private static final FluidInvTankChangeListener[] NO_LISTENERS = new FluidInvTankChangeListener[0];

	/**
	 * Sentinel value used during {@link #invalidateListeners()}.
	 */
	private static final FluidInvTankChangeListener[] INVALIDATING_LISTENERS = new FluidInvTankChangeListener[0];

	private FluidAmount capacity;
	private Supplier<FluidAmount> capacitySupplier = null;
	private final DefaultedList<FluidVolume> tanks;
	private FluidFilter filter;

	// TO-DO: Optimise this to cache more information!
	private final GroupedFluidInv groupedVersion = new GroupedFluidInvFixedWrapper(this);

	private FluidInvTankChangeListener ownerListener;

	private final Map<FluidInvTankChangeListener, ListenerRemovalToken> listeners
			= new Object2ObjectLinkedOpenCustomHashMap<>(Util.identityHashStrategy());

	// Should this use WeakReference instead of storing them directly?
	private FluidInvTankChangeListener[] bakedListeners = NO_LISTENERS;


	public BaseFluidContainer(int invSize, FluidAmount capacity) {
		this(invSize, capacity, ConstantFluidFilter.ANYTHING);
	}

	public BaseFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier) {
		this(invSize, capacitySupplier, ConstantFluidFilter.ANYTHING);
	}


	public BaseFluidContainer(int invSize, FluidAmount capacity, FluidFilter filter) {
		tanks = DefaultedList.ofSize(invSize, FluidVolumeUtil.EMPTY);
		this.capacity = capacity;
		this.filter = filter;
	}

	public BaseFluidContainer(int invSize, Supplier<FluidAmount> capacitySupplier, FluidFilter filter) {
		tanks = DefaultedList.ofSize(invSize, FluidVolumeUtil.EMPTY);
		this.capacity = capacitySupplier.get();
		this.capacitySupplier = capacitySupplier;
		this.filter = filter;
	}

	// AssemblyFluidContainer
	@Override
	public void setCapacity(FluidAmount capacity) {
		this.capacity = capacity;
	}

	@Override
	public FluidAmount getCapacity(int tank) {
		if (capacitySupplier != null) {
			return capacity = capacitySupplier.get();
		}
		return capacity;
	}

	// FixedFluidInv
	@Override
	public final int getTankCount() {
		return tanks.size();
	}


	@Override
	public FluidVolume getInvFluid(int tank) {
		return tanks.get(tank);
	}

	@Override
	public boolean isFluidValidForTank(int tank, FluidKey fluid) {
		return fluid.isEmpty() || filter.matches(fluid);
	}

	@Override
	public FluidFilter getFilterForTank(int tank) {
		return filter;
	}

	@Override
	public boolean setInvFluid(int tank, FluidVolume to, Simulation simulation) {
		if (isFluidValidForTank(tank, to.fluidKey) && !to.getAmount_F().isGreaterThan(getMaxAmount_F(tank))) {
			if (simulation == Simulation.ACTION) {
				FluidVolume before = tanks.get(tank);
				tanks.set(tank, to);
				fireTankChange(tank, before, to);
			}
			return true;
		}
		return false;
	}

	// Others
	@Override
	public GroupedFluidInv getGroupedInv() {
		return this.groupedVersion;
	}

	@Override
	public ListenerToken addListener(FluidInvTankChangeListener listener, ListenerRemovalToken removalToken) {
		if (bakedListeners == INVALIDATING_LISTENERS) {
			// It doesn't really make sense to add listeners while we are invalidating them
			return null;
		}
		ListenerRemovalToken previous = listeners.put(listener, removalToken);
		if (previous == null) {
			bakeListeners();
		} else {
			assert previous == removalToken : "The same listener object must be registered with the same removal token";
		}
		return () -> {
			ListenerRemovalToken token = listeners.remove(listener);
			if (token != null) {
				assert token == removalToken;
				bakeListeners();
				removalToken.onListenerRemoved();
			}
		};
	}

	/**
	 * Sets the owner listener callback, which is never removed from the listener list when
	 * {@link #invalidateListeners()} is called.
	 */
	@Override
	public void setOwnerListener(FluidInvTankChangeListener ownerListener) {
		this.ownerListener = ownerListener;
	}

	private void bakeListeners() {
		bakedListeners = listeners.keySet().toArray(new FluidInvTankChangeListener[0]);
	}

	public void invalidateListeners() {
		bakedListeners = INVALIDATING_LISTENERS;
		ListenerRemovalToken[] removalTokens = listeners.values().toArray(new ListenerRemovalToken[0]);
		listeners.clear();
		for (ListenerRemovalToken token : removalTokens) {
			token.onListenerRemoved();
		}
		bakedListeners = NO_LISTENERS;
	}

	protected final void fireTankChange(int tank, FluidVolume previous, FluidVolume current) {
		if (ownerListener != null) {
			ownerListener.onChange(this, tank, previous, current);
		}
		// Iterate over the previous array in case the listeners array is changed while we are iterating
		final FluidInvTankChangeListener[] baked = bakedListeners;
		for (FluidInvTankChangeListener listener : baked) {
			listener.onChange(this, tank, previous, current);
		}
	}

	// NBT support
	@Override
	public final CompoundTag toTag() {
		return toTag(new CompoundTag());
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag tanksTag = new ListTag();
		for (FluidVolume volume : tanks) {
			tanksTag.add(volume.toTag());
		}
		tag.put(TANKS_KEY, tanksTag);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		ListTag tanksTag = tag.getList(TANKS_KEY, new CompoundTag().getType());
		for (int i = 0; i < tanksTag.size() && i < tanks.size(); i++) {
			tanks.set(i, FluidVolume.fromTag(tanksTag.getCompound(i)));
		}
		for (int i = tanksTag.size(); i < tanks.size(); i++) {
			tanks.set(i, FluidVolumeUtil.EMPTY);
		}
	}


}
