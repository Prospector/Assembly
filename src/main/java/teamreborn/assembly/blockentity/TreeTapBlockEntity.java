package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Facing;
import prospector.silk.blockentity.FluidContainer;
import teamreborn.assembly.registry.AssemblyBlockEntities;
import teamreborn.assembly.registry.AssemblyFluids;

public class TreeTapBlockEntity extends BlockEntity implements Tickable {
	public TreeTapBlockEntity() {
		super(AssemblyBlockEntities.TREE_TAP_BLOCK_ENTITY);
	}

	@Override
	public void tick() {
		if (!world.isRemote && world.getTime() % (5 + world.getRandom().nextInt(15)) == 0) {
			BlockEntity downEntity = world.getBlockEntity(pos.offset(Facing.DOWN));
			if (downEntity instanceof FluidContainer) {
				((FluidContainer) downEntity).tryInsertFluid(Facing.UP, AssemblyFluids.SAP, 1);
			}
		}
	}
}
