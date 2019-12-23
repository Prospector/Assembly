package team.reborn.assembly.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import reborncore.common.fluid.container.GenericFluidContainer;

public class DipstickItem extends Item {
	public DipstickItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient()) {
			BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
			if (blockEntity instanceof GenericFluidContainer) {
				context.getPlayer().sendMessage(new LiteralText("Fluid: " + Registry.FLUID.getId(((GenericFluidContainer) blockEntity).getFluidInstance(context.getSide()).getFluid())));
				context.getPlayer().sendMessage(new LiteralText("Amount: " + ((GenericFluidContainer) blockEntity).getFluidInstance(context.getSide()).getAmount()));
				context.getPlayer().sendMessage(new LiteralText("Capacity: " + ((GenericFluidContainer) blockEntity).getCapacity(context.getSide())));
				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}
}
