package teamreborn.assembly.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import reborncore.common.fluid.container.GenericFluidContainer;
import teamreborn.assembly.block.TreeTapBlock;
import teamreborn.assembly.fluid.AssemblyFluids;
import teamreborn.assembly.util.FluidValues;
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
			if (world.getTime() % (20 + world.getRandom().nextInt(12)) == 0) {
				BlockEntity downEntity = world.getBlockEntity(pos.offset(Direction.DOWN));
				boolean pouring = downEntity instanceof GenericFluidContainer;
				if (pouring) {
					pouring = ((GenericFluidContainer<Direction>) downEntity).canInsertFluid(Direction.UP, pouringFluid, FluidValues.THOUSANDTH_BUCKET);
					if (pouring) {
						((GenericFluidContainer<Direction>) downEntity).insertFluid(Direction.UP, pouringFluid, FluidValues.THOUSANDTH_BUCKET);
					}
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
