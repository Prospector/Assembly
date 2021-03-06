package com.terraformersmc.assembly.item.exoframe;

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.RawFluidTagFilter;
import alexiil.mc.lib.attributes.fluid.item.ItemBasedSingleFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.terraformersmc.assembly.fluid.AssemblyFluids;
import com.terraformersmc.assembly.item.CustomArmorTexture;
import com.terraformersmc.assembly.tag.AssemblyFluidTags;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ExoframePieceItem extends ArmorItem implements AttributeProviderItem, CustomArmorTexture {
	private static final FluidAmount STEAM_TANK_CAPACITY = FluidAmount.BUCKET.roundedMul(8);
	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
//	private static final ModelOverride STEAM_TANK_PROPERTY_GETTER = (stack, world, entity) -> (float) getFluidInfo(stack).fluid.getAmount_F().div(STEAM_TANK_CAPACITY).asInexactDouble();
	private static final Identifier TEXTURE_ID = AssemblyConstants.Ids.EXOFRAME;
	public static final FluidFilter STEAM_FITLTER = new RawFluidTagFilter(AssemblyFluidTags.STEAM);

	public ExoframePieceItem(EquipmentSlot slot, Settings settings) {
		super(AssemblyConstants.ArmorMaterials.EXOFRAME, slot, settings);
//        this.addPropertyGetter(AssemblyConstants.Ids.STEAM_TANK, STEAM_TANK_PROPERTY_GETTER);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		FluidVolumeUtil.move(FluidAttributes.EXTRACTABLE.get(context.getWorld(), context.getBlockPos()), FluidAttributes.INSERTABLE.get(context.getStack()), FluidAmount.BOTTLE);
		return super.useOnBlock(context);
	}

	@Override
	public void addAllAttributes(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess, ItemAttributeList<?> to) {
		to.offer(new ExoframeFluidTank(stack, excess));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		ItemBasedSingleFluidInv.HeldFluidInfo fluid = getFluidInfo(stack);
		MutableText text = new LiteralText("");
		text.append(FluidKeys.get(AssemblyFluids.STEAM).name);
		text.append(new LiteralText(": "));
		text.append(fluid.fluid.localizeInTank(STEAM_TANK_CAPACITY));
		tooltip.add(text);
		if (context.isAdvanced()) {
//			tooltip.add(new LiteralText("assembly:steam_tank property value=" + STEAM_TANK_PROPERTY_GETTER.call(stack, world, null)));
		}
	}

	@Override
	public Identifier getId(ArmorItem armorItem, boolean lowerParts, @Nullable String suffix) {
		return TEXTURE_ID;
	}

	private static ItemBasedSingleFluidInv.HeldFluidInfo getFluidInfo(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(FLUIDS_KEY, /* TODO: Where are these constants stored? */ 10)) {
			return new ItemBasedSingleFluidInv.HeldFluidInfo(FluidVolumeUtil.EMPTY, STEAM_TANK_CAPACITY);
		}
		FluidVolume volume = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
		return new ItemBasedSingleFluidInv.HeldFluidInfo(volume, STEAM_TANK_CAPACITY);
	}

	public class ExoframeFluidTank extends ItemBasedSingleFluidInv {

		public ExoframeFluidTank(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess) {
			super(stack, excess);
		}

		@Override
		protected boolean isInvalid(ItemStack s) {
			return s.getCount() != 1 || s.getItem() != ExoframePieceItem.this;
		}

		@Override
		public FluidFilter getInsertionFilter() {
			return STEAM_FITLTER;
		}

		@Override
		protected ItemBasedSingleFluidInv.HeldFluidInfo getInfo(ItemStack stack) {
			return getFluidInfo(stack);
		}

		@Override
		protected ItemStack writeToStack(ItemStack stack, FluidVolume fluid) {
			if (fluid.isEmpty()) {
				stack.removeSubTag(FLUIDS_KEY);
			} else {
				stack.putSubTag(FLUIDS_KEY, fluid.toTag());
			}
			return stack;
		}
	}
}
