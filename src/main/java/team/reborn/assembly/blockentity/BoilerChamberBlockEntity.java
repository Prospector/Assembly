package team.reborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BoilerChamberBlockEntity extends BlockEntity {

	private BoilerBlockEntity boiler;
	private BlockPos boilerPos;

	public BoilerChamberBlockEntity() {
		super(AssemblyBlockEntities.BOILER_CHAMBER);
	}

	public void updateBoiler(BlockPos pos) {
		if (world == null || pos == null) {
			return;
		}
		boilerPos = pos;
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BoilerBlockEntity) {
			boiler = (BoilerBlockEntity) blockEntity;
		} else {
			boiler = null;
		}
	}

	public BoilerBlockEntity getBoiler() {
		return boiler;
	}

	public BlockPos getBoilerPos() {
		return boilerPos;
	}
}
