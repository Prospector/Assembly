package teamreborn.assembly.util.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.FacingProperty;

public class MachinePlacementContext extends ItemPlacementContext {
	public final Block block;
	public final FacingProperty property;

	public MachinePlacementContext(Block block, FacingProperty property, ItemPlacementContext context) {
		super(context);
		this.block = block;
		this.property = property;
	}
}
