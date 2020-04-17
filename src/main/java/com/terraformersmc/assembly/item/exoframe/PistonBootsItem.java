package com.terraformersmc.assembly.item.exoframe;

import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.item.CustomArmorTextureForStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import com.terraformersmc.assembly.mixintf.FallDamageTransformingBoots;
import com.terraformersmc.assembly.util.AssemblyConstants;

import javax.annotation.Nullable;

public class PistonBootsItem extends ExoframePieceItem implements ExoframeModule, FallDamageTransformingBoots, CustomArmorTextureForStack {
	private static final String EXTENDED_KEY = AssemblyConstants.NbtKeys.EXTENDED;

	public PistonBootsItem(Settings settings) {
		super(EquipmentSlot.FEET, settings);
	}

	@Override
	public float transformFallDamage(LivingEntity entity, ItemStack stack, float damageMultiplier) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(EXTENDED_KEY) && tag.getBoolean(EXTENDED_KEY)) {
			return damageMultiplier * 0.1F;
		}
		return damageMultiplier;
	}

	@Override
	public float transformJumpVelocity(LivingEntity entity, ItemStack stack, float defaultMultiplier) {
		if (extendPiston(entity, stack, false)) {
			return defaultMultiplier * 1.5F;
		}
		return defaultMultiplier;
	}

	@Override
	public boolean onEntityLand(BlockView world, Entity entity, ItemStack stack) {
		contractPiston(entity, stack);
		extendPiston(entity, stack, true);
		return true;
	}

	public boolean extendPiston(Entity entity, ItemStack stack, boolean changeVelocity) {
		boolean isExtended = false;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(EXTENDED_KEY)) {
			isExtended = tag.getBoolean(EXTENDED_KEY);
		}
		if (isExtended) {
			return false;
		} else {
			if (changeVelocity) {
				Vec3d vec3d = entity.getVelocity();
				if (vec3d.y < 0) {
					double d = vec3d.y > -0.8 ? 0D : 1D;
					entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
					if (entity.getVelocity().y > 0.2) {
						tag.putBoolean(EXTENDED_KEY, true);
						stack.setTag(tag);
						entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, (float) MathHelper.clamp(0.2F * entity.getVelocity().y, 0.0F, 0.3F), RANDOM.nextFloat() * 0.25F + 0.8F);
					}
				}
			} else {
				tag.putBoolean(EXTENDED_KEY, true);
				stack.setTag(tag);
				entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, MathHelper.clamp(0.2F, 0.0F, 0.3F), RANDOM.nextFloat() * 0.25F + 0.8F);
			}
			return true;
		}
	}

	public void contractPiston(Entity entity, ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(EXTENDED_KEY) && tag.getBoolean(EXTENDED_KEY)) {
			tag.putBoolean(EXTENDED_KEY, false);
			stack.setTag(tag);
			entity.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 0.2F, RANDOM.nextFloat() * 0.25F + 0.8F);
		}
	}

	@Override
	public Identifier getId(ItemStack stack, boolean lowerParts, @Nullable String suffix) {
		CompoundTag tag = stack.getOrCreateTag();
		return new Identifier(Assembly.MOD_ID, "piston_boots_" + (tag.contains(EXTENDED_KEY) && tag.getBoolean(EXTENDED_KEY) ? "extended" : "contracted"));
	}
}
