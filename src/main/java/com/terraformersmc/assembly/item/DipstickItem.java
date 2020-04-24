package com.terraformersmc.assembly.item;

import com.terraformersmc.assembly.block.BoilerBlock;
import com.terraformersmc.assembly.blockentity.*;
import com.terraformersmc.assembly.util.interaction.interactable.InteractionBypass;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Arrays;

public class DipstickItem extends Item implements InteractionBypass {
	public DipstickItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (!context.getWorld().isClient()) {
			BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
			Formatting[] formats = new Formatting[]{Formatting.AQUA, Formatting.BLUE, Formatting.GOLD, Formatting.GRAY, Formatting.GREEN, Formatting.RED, Formatting.LIGHT_PURPLE, Formatting.YELLOW, Formatting.WHITE};
			Formatting format = formats[context.getPlayer().getRandom().nextInt(formats.length)];
			if (blockEntity instanceof BoilerBlockEntity) {
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Input Fluid: " + Registry.FLUID.getId(((BoilerBlockEntity) blockEntity).getInputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Input Amount: " + ((BoilerBlockEntity) blockEntity).getInputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Input Capacity: " + ((BoilerBlockEntity) blockEntity).getInputTank().getMaxAmount_F(0).toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Output Fluid: " + Registry.FLUID.getId(((BoilerBlockEntity) blockEntity).getOutputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Output Amount: " + ((BoilerBlockEntity) blockEntity).getOutputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Output Capacity: " + ((BoilerBlockEntity) blockEntity).getOutputTank().getMaxAmount_F(0).toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Recipe Increment: " + ((BoilerBlockEntity) blockEntity).getRecipeIncrement().toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof BoilerChamberBlockEntity) {
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Fluid: " + Registry.FLUID.getId(((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Amount: " + ((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Capacity: " + ((BoilerChamberBlockEntity) blockEntity).getBoiler().getOutputTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof SteamPressBlockEntity) {
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Fluid: " + Registry.FLUID.getId(((SteamPressBlockEntity) blockEntity).getTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Amount: " + ((SteamPressBlockEntity) blockEntity).getTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Capacity: " + ((SteamPressBlockEntity) blockEntity).getTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof FluidBarrelBlockEntity) {
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Fluid: " + Registry.FLUID.getId(((FluidBarrelBlockEntity) blockEntity).getTank().getInvFluid(0).getRawFluid())));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Amount: " + ((FluidBarrelBlockEntity) blockEntity).getTank().getInvFluid(0).getAmount_F().toDisplayString()));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Capacity: " + ((FluidBarrelBlockEntity) blockEntity).getTank().getMaxAmount_F(0).toDisplayString()));
				return ActionResult.SUCCESS;
			}
			if (blockEntity instanceof FluidHopperBlockEntity) {
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Fluids: " + Arrays.toString(new Identifier[]{Registry.FLUID.getId(((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(0).getRawFluid()), Registry.FLUID.getId(((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(1).getRawFluid()), Registry.FLUID.getId(((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(2).getRawFluid()), Registry.FLUID.getId(((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(3).getRawFluid()), Registry.FLUID.getId(((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(4).getRawFluid())})));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Amounts: " + Arrays.toString(new String[]{((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(0).getAmount_F().toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(1).getAmount_F().toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(2).getAmount_F().toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(3).getAmount_F().toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getInvFluid(4).getAmount_F().toDisplayString()})));
				context.getPlayer().sendSystemMessage(new LiteralText(format + "Capacites: " + Arrays.toString(new String[]{((FluidHopperBlockEntity) blockEntity).getTank().getMaxAmount_F(0).toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getMaxAmount_F(1).toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getMaxAmount_F(2).toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getMaxAmount_F(3).toDisplayString(), ((FluidHopperBlockEntity) blockEntity).getTank().getMaxAmount_F(4).toDisplayString()})));
				return ActionResult.SUCCESS;
			}
		}
		return super.useOnBlock(context);
	}

	@Override
	public boolean bypassesInteractions(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return state.getBlock() instanceof BoilerBlock;
	}
}
