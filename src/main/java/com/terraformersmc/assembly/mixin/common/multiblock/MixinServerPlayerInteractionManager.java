package com.terraformersmc.assembly.mixin.common.multiblock;

import com.terraformersmc.assembly.block.PressBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class MixinServerPlayerInteractionManager {
	@Shadow
	public ServerWorld world;

	@Shadow
	private BlockPos miningPos;

	@Redirect(method = "processBlockBreakingAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;equals(Ljava/lang/Object;)Z"))
	private boolean onIsCurrentlyBreaking(BlockPos pos, Object miningPos) {
		boolean fromUpper = pos.up().equals(miningPos);
		boolean fromLower = pos.down().equals(miningPos);
		if (fromUpper || fromLower) {
			BlockState newState;
			BlockState oldState;
			if ((oldState = world.getBlockState((BlockPos) miningPos)).getBlock() instanceof PressBlock && (newState = world.getBlockState(pos)).getBlock() instanceof PressBlock) {
				if (fromUpper && oldState.get(PressBlock.HALF) == DoubleBlockHalf.UPPER && newState.get(PressBlock.HALF) == DoubleBlockHalf.LOWER) {
					return true;
				} else if (fromLower && oldState.get(PressBlock.HALF) == DoubleBlockHalf.LOWER && newState.get(PressBlock.HALF) == DoubleBlockHalf.UPPER) {
					return true;
				}
			}
		}
		return pos.equals(miningPos);
	}
}
