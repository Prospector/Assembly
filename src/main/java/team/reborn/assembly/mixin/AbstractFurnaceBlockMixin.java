package team.reborn.assembly.mixin;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.reborn.assembly.block.AssemblyBlocks;

@Mixin(AbstractFurnaceBlock.class)
public abstract class AbstractFurnaceBlockMixin {

	@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
	private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
		if (((Object) this) == Blocks.FURNACE && hit.getSide() == Direction.UP && player.getStackInHand(hand).getItem() == AssemblyBlocks.BOILER_CHAMBER.asItem()) {
			info.setReturnValue(ActionResult.PASS);
		}
	}
}
