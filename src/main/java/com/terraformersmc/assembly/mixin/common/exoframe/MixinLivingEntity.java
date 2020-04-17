package com.terraformersmc.assembly.mixin.common.exoframe;

import com.terraformersmc.assembly.mixintf.FallDamageTransformingBoots;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Inject(method = "getJumpVelocity", at = @At("HEAD"), cancellable = true)
	private void hijackJumpVelocity(CallbackInfoReturnable<Float> info) {
		if (getEquippedStack(EquipmentSlot.FEET).getItem() instanceof FallDamageTransformingBoots) {
			info.setReturnValue(((FallDamageTransformingBoots) getEquippedStack(EquipmentSlot.FEET).getItem()).transformJumpVelocity((LivingEntity) (Object) this, getEquippedStack(EquipmentSlot.FEET), 0.42F) * this.getJumpVelocityMultiplier())
			;
		}
	}
}
