package team.reborn.assembly.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.blockentity.BoilerBlockEntity;
import team.reborn.assembly.blockentity.BoilerChamberBlockEntity;

public class DipstickItem extends Item {
	public DipstickItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient()) {
			BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
			if (blockEntity instanceof BoilerBlockEntity) {
				context.getPlayer().sendMessage(new LiteralText("Input Fluid: " + Registry.FLUID.getId(((BoilerBlockEntity) blockEntity).getInputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText("Input Amount: " + ((BoilerBlockEntity) blockEntity).getInputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText("Input Capacity: " + ((BoilerBlockEntity) blockEntity).getInputTank().getCapacity(0).toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText("Output Fluid: " + Registry.FLUID.getId(((BoilerBlockEntity) blockEntity).getOutputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText("Output Amount: " + ((BoilerBlockEntity) blockEntity).getOutputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText("Output Capacity: " + ((BoilerBlockEntity) blockEntity).getOutputTank().getCapacity(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof BoilerChamberBlockEntity) {
				context.getPlayer().sendMessage(new LiteralText("Output Fluid: " + Registry.FLUID.getId(((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText("Output Amount: " + ((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText("Output Capacity: " + ((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getCapacity(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}
}
