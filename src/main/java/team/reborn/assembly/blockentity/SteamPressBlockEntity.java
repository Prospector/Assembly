package team.reborn.assembly.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import team.reborn.assembly.block.SteamPressBlock;

import java.util.List;

public class SteamPressBlockEntity extends BlockEntity implements Tickable {

	private int progressTime;
	private int resetTime;

	public SteamPressBlockEntity() {
		super(AssemblyBlockEntities.STEAM_PRESS);
	}

	@Override
	public void tick() {
		if (world != null) {
			if (world.getBlockState(pos).get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
				pushEntities();
			}
		}
	}

	private void pushEntities() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() instanceof SteamPressBlock) {
			Box armBox = this.getArmVoxelShape(DoubleBlockHalf.UPPER).getBoundingBox().offset(this.pos);
			List<Entity> entities = this.world.getEntities(null, armBox);
			if (!entities.isEmpty()) {
				for (Entity entity : entities) {
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						Box entityBox = entity.getBoundingBox();
						entity.move(MovementType.SHULKER_BOX, new Vec3d(0, armBox.y2 - entityBox.y1 + 0.01D, 0));
					}
				}
			}
		}
	}

	public VoxelShape getArmVoxelShape(DoubleBlockHalf half) {
		int speed = 75;
		long time = world.getTime() % speed;
		float lowestPosition = 9 / 16F; //9/16 because the lowest the arm should go is 9 pixels down.
		lowestPosition = lowestPosition / 2; //Divide by 2 because the sine wave will go 9/16 above AND below when the difference between the min and the max should be 9/16.
		double offset = lowestPosition * Math.sin(2 * Math.PI / speed * (time)) - lowestPosition;
		return SteamPressBlock.ARM_SHAPE.offset(0, half == DoubleBlockHalf.LOWER ? offset + 1 : offset, 0);
	}

}
