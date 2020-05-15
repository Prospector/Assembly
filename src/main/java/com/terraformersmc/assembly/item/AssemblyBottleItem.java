package com.terraformersmc.assembly.item;

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.AbstractItemBasedAttribute;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.terraformersmc.assembly.fluid.AssemblyFluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AssemblyBottleItem extends Item implements AttributeProviderItem {
	private final FluidKey fluidKey;

	public AssemblyBottleItem(AssemblyFluid fluid, Item.Settings settings) {
		super(settings);
		this.fluidKey = FluidKeys.get(fluid);
	}

	@Override
	public void addAllAttributes(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess, ItemAttributeList<?> to) {
		to.offer(new BottleTank(stack, excess));
	}

	public class BottleTank extends AbstractItemBasedAttribute implements FluidExtractable {

		public BottleTank(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess) {
			super(stack, excess);
		}

		@Override
		public FluidVolume attemptExtraction(FluidFilter filter, FluidAmount maxAmount, Simulation simulation) {
			if (filter.matches(fluidKey) && maxAmount.isGreaterThanOrEqual(FluidAmount.BOTTLE)) {
				ItemStack stack = stackRef.get().copy();
				stack.decrement(1);
				setStacks(simulation, stack, new ItemStack(Items.GLASS_BOTTLE, 1));
				return fluidKey.withAmount(FluidAmount.BOTTLE);
			}
			return FluidVolumeUtil.EMPTY;
		}
	}
}
