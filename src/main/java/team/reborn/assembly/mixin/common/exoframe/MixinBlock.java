package team.reborn.assembly.mixin.common.exoframe;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.reborn.assembly.mixintf.FallDamageTransformingBoots;

@Mixin(Block.class)
public class MixinBlock {

	@Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
	private void hijackLandedUpon(World world, BlockPos pos, Entity entity, float distance, CallbackInfo info) {
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET).getItem() instanceof FallDamageTransformingBoots) {
			entity.handleFallDamage(distance, ((FallDamageTransformingBoots) ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET).getItem()).transformFallDamage((LivingEntity) entity, ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET), 1.0F));
			info.cancel();
		}
	}

	@Inject(method = "onEntityLand", at = @At("HEAD"), cancellable = true)
	private void hijackEntityLand(BlockView world, Entity entity, CallbackInfo info) {
		if (entity instanceof LivingEntity && ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET).getItem() instanceof FallDamageTransformingBoots && ((FallDamageTransformingBoots) ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET).getItem()).onEntityLand(world, entity, ((LivingEntity) entity).getEquippedStack(EquipmentSlot.FEET))) {
			info.cancel();
		}
	}
}
