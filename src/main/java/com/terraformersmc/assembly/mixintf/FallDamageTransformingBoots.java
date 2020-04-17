package com.terraformersmc.assembly.mixintf;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;

public interface FallDamageTransformingBoots {
	float transformFallDamage(LivingEntity entity, ItemStack stack, float damageMultiplier);

	default float transformJumpVelocity(LivingEntity entity, ItemStack stack, float defaultMultiplier) {
		return defaultMultiplier;
	}

	/**
	 * @return overrides vanilla handling
	 */
	default boolean onEntityLand(BlockView world, Entity entity, ItemStack stack) {
		return false;
	}
}
