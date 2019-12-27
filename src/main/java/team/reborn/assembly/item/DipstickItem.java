package team.reborn.assembly.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.TextFormat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.blockentity.BoilerBlockEntity;
import team.reborn.assembly.blockentity.BoilerChamberBlockEntity;
import team.reborn.assembly.blockentity.SteamPressBlockEntity;
import team.reborn.assembly.blockentity.WoodenBarrelBlockEntity;

public class DipstickItem extends Item {
	public DipstickItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient()) {
			BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
			TextFormat[] formats = new TextFormat[]{TextFormat.AQUA, TextFormat.BLUE, TextFormat.GOLD, TextFormat.GRAY, TextFormat.GREEN, TextFormat.RED, TextFormat.LIGHT_PURPLE, TextFormat.YELLOW, TextFormat.WHITE};
			TextFormat format = formats[context.getPlayer().getRandom().nextInt(formats.length)];
			if (blockEntity instanceof BoilerBlockEntity) {
				context.getPlayer().sendMessage(new LiteralText(format + "Input Fluid: " + Registry.FLUID.getId(((BoilerBlockEntity) blockEntity).getInputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText(format + "Input Amount: " + ((BoilerBlockEntity) blockEntity).getInputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText(format + "Input Capacity: " + ((BoilerBlockEntity) blockEntity).getInputTank().getMaxAmount_F(0).toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText(format + "Output Fluid: " + Registry.FLUID.getId(((BoilerBlockEntity) blockEntity).getOutputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText(format + "Output Amount: " + ((BoilerBlockEntity) blockEntity).getOutputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText(format + "Output Capacity: " + ((BoilerBlockEntity) blockEntity).getOutputTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof BoilerChamberBlockEntity) {
				context.getPlayer().sendMessage(new LiteralText(format + "Fluid: " + Registry.FLUID.getId(((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText(format + "Amount: " + ((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText(format + "Capacity: " + ((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof SteamPressBlockEntity) {
				context.getPlayer().sendMessage(new LiteralText(format + "Fluid: " + Registry.FLUID.getId(((SteamPressBlockEntity) blockEntity).getInputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText(format + "Amount: " + ((SteamPressBlockEntity) blockEntity).getInputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText(format + "Capacity: " + ((SteamPressBlockEntity) blockEntity).getInputTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof WoodenBarrelBlockEntity) {
				context.getPlayer().sendMessage(new LiteralText(format + "Fluid: " + Registry.FLUID.getId(((WoodenBarrelBlockEntity) blockEntity).getTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendMessage(new LiteralText(format + "Amount: " + ((WoodenBarrelBlockEntity) blockEntity).getTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendMessage(new LiteralText(format + "Capacity: " + ((WoodenBarrelBlockEntity) blockEntity).getTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}
}
