package team.reborn.assembly.util.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;

public class MachinePlacementContext extends ItemPlacementContext {
	public final Block block;
	public final DirectionProperty property;

	public MachinePlacementContext(Block block, DirectionProperty property, ItemPlacementContext context) {
		super(context);
		this.block = block;
		this.property = property;
	}
}
