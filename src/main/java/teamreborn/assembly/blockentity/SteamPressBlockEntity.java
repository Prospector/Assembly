package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class SteamPressBlockEntity extends BlockEntity implements Tickable {
	public SteamPressBlockEntity() {
		super(AssemblyBlockEntities.STEAM_PRESS);
	}

	@Override
	public void tick() {
		if (world != null) {

		}
	}
}
