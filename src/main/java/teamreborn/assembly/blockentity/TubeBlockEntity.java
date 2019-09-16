package teamreborn.assembly.blockentity;

import io.github.prospector.silk.fluid.DropletValues;
import io.github.prospector.silk.fluid.FluidInstance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import teamreborn.assembly.api.AttachableFluidContainer;
import teamreborn.assembly.block.TubeBlock;

public class TubeBlockEntity extends BlockEntity implements AttachableFluidContainer {

	public FluidInstance fluid = FluidInstance.EMPTY;

	public TubeBlockEntity() {
		super(AssemblyBlockEntities.TREE_TAP);
	}

	@Override
	public int getMaxCapacity() {
		return DropletValues.BLOCK / 6;
	}

	@Override
	public boolean canInsertFluid(Direction fromSide, Fluid fluid, int amount) {
		return false;
	}

	@Override
	public boolean canExtractFluid(Direction fromSide, Fluid fluid, int amount) {
		return false;
	}

	@Override
	public void insertFluid(Direction fromSide, Fluid fluid, int amount) {

	}

	@Override
	public void extractFluid(Direction fromSide, Fluid fluid, int amount) {

	}

	@Override
	public void setFluid(Direction fromSide, FluidInstance instance) {

	}

	@Override
	public boolean canAttachForInsertion(Direction side) {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			return TubeBlock.getAvailableConnection(state, side) != null || TubeBlock.getConnection(state, side) != null;
		}
		return false;
	}

	@Override
	public boolean canAttachForExtraction(Direction side) {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			return TubeBlock.getAvailableConnection(state, side) != null || TubeBlock.getConnection(state, side) != null;
		}
		return false;
	}

	@Override
	public FluidInstance[] getFluids(Direction fromSide) {
		return new FluidInstance[0];
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		return super.toTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
	}
}
