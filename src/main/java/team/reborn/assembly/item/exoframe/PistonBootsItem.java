package team.reborn.assembly.item.exoframe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import team.reborn.assembly.mixintf.FallDamageTransformingBoots;

public class PistonBootsItem extends ExoframePieceItem implements ExoframeModule, FallDamageTransformingBoots {
	public PistonBootsItem(Settings settings) {
		super(EquipmentSlot.FEET, settings);
	}

	@Override
	public float transformFallDamage(LivingEntity entity, ItemStack stack, float damageMultiplier) {
		return damageMultiplier * 0.1F;
	}

	@Override
	public float transformJumpVelocity(LivingEntity entity, ItemStack stack, float defaultMultiplier) {
		entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 0.2F, RANDOM.nextFloat() * 0.25F + 0.8F);
		return defaultMultiplier * 1.5F;
	}

	@Override
	public boolean onEntityLand(BlockView world, Entity entity) {
		Vec3d vec3d = entity.getVelocity();
		if (vec3d.y < 0) {
			double d = vec3d.y > -0.6 ? 0.2D : 0.9D;
			entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
			System.out.println(entity.getVelocity().y);
			if (entity.getVelocity().y > 0.2) {
				entity.playSound(SoundEvents.BLOCK_PISTON_EXTEND, (float) MathHelper.clamp(0.2F * entity.getVelocity().y, 0.0F, 0.3F), RANDOM.nextFloat() * 0.25F + 0.8F);
			}
		}
		return true;
	}
}
