package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;

public abstract class MachineBaseBlockEntity extends BlockEntity implements Tickable {
	public MachineBaseBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void tick() {

	}
}
