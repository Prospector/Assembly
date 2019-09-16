package teamreborn.assembly.api;

import io.github.prospector.silk.fluid.FluidContainer;
import net.minecraft.util.math.Direction;

public interface AttachableFluidContainer extends FluidContainer {
	public boolean canAttachForInsertion(Direction side);

	public boolean canAttachForExtraction(Direction side);

	public default boolean canAttach(Direction side) {
		return canAttachForInsertion(side) || canAttachForExtraction(side);
	}
}
