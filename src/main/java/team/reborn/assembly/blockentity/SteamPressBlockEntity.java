package team.reborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.Tickable;
import net.minecraft.util.shape.VoxelShape;
import team.reborn.assembly.block.SteamPressBlock;

public class SteamPressBlockEntity extends BlockEntity implements Tickable {

	private int progressTime;
	private int resetTime;

	public SteamPressBlockEntity() {
		super(AssemblyBlockEntities.STEAM_PRESS);
	}

	@Override
	public void tick() {
		if (world != null) {

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
