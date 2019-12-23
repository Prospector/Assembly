package team.reborn.assembly.api;

import net.minecraft.util.math.Direction;
import reborncore.common.fluid.container.GenericFluidContainer;

public interface AttachableFluidContainer extends GenericFluidContainer<Direction> {
	boolean canAttachForInsertion(Direction side);

	boolean canAttachForExtraction(Direction side);

	default boolean canAttach(Direction side) {
		return canAttachForInsertion(side) || canAttachForExtraction(side);
	}
}
