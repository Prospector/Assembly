package teamreborn.assembly.blockentity;

import io.github.prospector.silk.fluid.FluidContainer;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import teamreborn.assembly.block.TreeTapBlock;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.util.block.AssemblyProperties;

public class TreeTapBlockEntity extends BlockEntity implements Tickable {
	public Fluid pouringFluid = Fluids.EMPTY;

	public TreeTapBlockEntity() {
		super(AssemblyBlockEntities.TREE_TAP);
	}

	@Override
	public void tick() {
		pouringFluid = AssemblyFluids.LATEX.getStill();
		if (world != null && !world.isClient) {
			if (world.getTime() % (25 + world.getRandom().nextInt(15)) == 0) {
				BlockEntity downEntity = world.getBlockEntity(pos.offset(Direction.DOWN));
				boolean pouring = downEntity instanceof FluidContainer;
				if (pouring) {
					pouring = ((FluidContainer) downEntity).tryInsertFluid(Direction.UP, pouringFluid, 1, ActionType.PERFORM);
				}
				BlockState state = world.getBlockState(pos);
				if (state.getBlock() instanceof TreeTapBlock && state.get(AssemblyProperties.POURING) != pouring) {
					world.setBlockState(pos, state.with(AssemblyProperties.POURING, pouring));
				}
			}
		}
	}

	public Fluid getPouringFluid() {
		return pouringFluid;
	}
}
