package com.terraformersmc.assembly.mixin.client.multiblock;

import com.terraformersmc.assembly.block.SteamPressBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
	@Shadow
	@Final
	private ClientPlayNetworkHandler networkHandler;

	@Shadow
	private BlockPos currentBreakingPos;

	@Inject(method = "isCurrentlyBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;equals(Ljava/lang/Object;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onIsCurrentlyBreaking(BlockPos pos, CallbackInfoReturnable<Boolean> info, boolean bl) {
		boolean fromUpper = pos.up().equals(currentBreakingPos);
		boolean fromLower = pos.down().equals(currentBreakingPos);
		if (fromUpper || fromLower) {
			BlockState newState;
			BlockState oldState;
			if ((oldState = networkHandler.getWorld().getBlockState(currentBreakingPos)).getBlock() instanceof SteamPressBlock && (newState = networkHandler.getWorld().getBlockState(pos)).getBlock() instanceof SteamPressBlock) {
				if (fromUpper && oldState.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER && newState.get(SteamPressBlock.HALF) == DoubleBlockHalf.LOWER) {
					info.setReturnValue(bl);
				} else if (fromLower && oldState.get(SteamPressBlock.HALF) == DoubleBlockHalf.LOWER && newState.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
					info.setReturnValue(bl);
				}
			}
		}
	}
}
