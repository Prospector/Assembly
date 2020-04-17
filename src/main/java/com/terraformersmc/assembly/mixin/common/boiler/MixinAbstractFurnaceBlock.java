package com.terraformersmc.assembly.mixin.common.boiler;

import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlock.class)
public abstract class MixinAbstractFurnaceBlock {

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
		if (((Object) this) == Blocks.FURNACE && hit.getSide() == Direction.UP && player.getStackInHand(hand).getItem() == AssemblyBlocks.BOILER_CHAMBER.asItem()) {
			info.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(method = "onPlaced", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
	private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo info) {
		if (((Object) this) == Blocks.FURNACE && stack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BoilerBlockEntity) {
				((BoilerBlockEntity) blockEntity).setCustomName(stack.getName());
			}
		}
	}
}
