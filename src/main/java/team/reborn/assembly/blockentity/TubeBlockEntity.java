package team.reborn.assembly.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import team.reborn.assembly.block.TubeBlock;
import team.reborn.assembly.api.AttachableFluidContainer;

import javax.annotation.Nonnull;

public class TubeBlockEntity extends BlockEntity implements AttachableFluidContainer {

	public FluidInstance fluid = FluidInstance.EMPTY;

	public TubeBlockEntity() {
		super(AssemblyBlockEntities.TREE_TAP);
	}

	@Override
	public void setFluid(Direction type, @Nonnull FluidInstance fluid) {
		this.fluid = fluid;
	}

	@Nonnull
	@Override
	public FluidInstance getFluidInstance(Direction type) {
		return fluid;
	}

	@Override
	public FluidValue getCapacity(Direction direction) {
		return FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 8);
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
	public CompoundTag toTag(CompoundTag tag) {
		return super.toTag(tag);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
	}
}
