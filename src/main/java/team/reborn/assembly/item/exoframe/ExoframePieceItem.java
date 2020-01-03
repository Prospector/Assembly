package team.reborn.assembly.item.exoframe;

import alexiil.mc.lib.attributes.AttributeProviderItem;
import alexiil.mc.lib.attributes.ItemAttributeList;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import team.reborn.assembly.util.AssemblyConstants;
import team.reborn.assembly.util.fluid.IOFluidContainer;
import team.reborn.assembly.util.fluid.SimpleIOFluidContainer;

import javax.annotation.Nullable;
import java.util.List;

public class ExoframePieceItem extends ArmorItem implements AttributeProviderItem {
	private static final FluidAmount STEAM_TANK_CAPACITY = FluidAmount.BUCKET.roundedMul(8);
	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final ItemPropertyGetter STEAM_TANK_PROPERTY_GETTER = (stack, world, entity) -> MathHelper.clamp((float) FluidAttributes.FIXED_INV_VIEW.get(stack).getInvFluid(0).getAmount_F().asInexactDouble() / (float) STEAM_TANK_CAPACITY.asInexactDouble(), 0.0F, 1.0F);

	public ExoframePieceItem(EquipmentSlot slot, Settings settings) {
		super(AssemblyConstants.ArmorMaterials.EXOFRAME, slot, settings);
		addPropertyGetter(AssemblyConstants.Ids.STEAM_TANK, STEAM_TANK_PROPERTY_GETTER);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		FluidVolumeUtil.move(FluidAttributes.EXTRACTABLE.get(context.getWorld(), context.getBlockPos()), FluidAttributes.INSERTABLE.get(context.getStack()), FluidAmount.BOTTLE);
		return super.useOnBlock(context);
	}

	@Override
	public void addAllAttributes(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess, ItemAttributeList<?> to) {
		IOFluidContainer tank = new SimpleIOFluidContainer(1, STEAM_TANK_CAPACITY);
		tank.addListener((inv, tank1, previous, current) -> {
			CompoundTag tag = stack.get().getOrCreateTag().copy();
			tag.put(FLUIDS_KEY, tank.toTag());
		}, () -> {
		});
		CompoundTag tag = stack.get().getOrCreateTag().copy();
		if (tag.contains(FLUIDS_KEY)) {
			tank.fromTag(tag.getCompound(FLUIDS_KEY));
		}
		to.offer(tank);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(new LiteralText("Steam: " + FluidAttributes.FIXED_INV_VIEW.get(stack).getInvFluid(0).getAmount_F().toDisplayString() + " / " + FluidAttributes.FIXED_INV_VIEW.get(stack).getMaxAmount_F(0).toDisplayString()));
		if (context.isAdvanced()) {
			tooltip.add(new LiteralText("assembly:steam_tank property value=" + STEAM_TANK_PROPERTY_GETTER.call(stack, world, null)));
		}
	}
}
