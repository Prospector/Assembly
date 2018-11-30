package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.state.StateFactory;
import net.minecraft.util.MapColor;
import net.minecraft.util.math.Facing;
import teamreborn.assembly.api.SapSource;
import teamreborn.assembly.util.AssemblyProperties;

public class RubberLogBlock extends LogBlock implements SapSource {

	public RubberLogBlock(MapColor mapColor, Builder builder) {
		super(mapColor, builder);
		setDefaultState(this.getDefaultState()
			.with(AssemblyProperties.NORTH_SAP, false)
			.with(AssemblyProperties.SOUTH_SAP, false)
			.with(AssemblyProperties.WEST_SAP, false)
			.with(AssemblyProperties.EAST_SAP, false));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.with(AssemblyProperties.NORTH_SAP);
		builder.with(AssemblyProperties.SOUTH_SAP);
		builder.with(AssemblyProperties.WEST_SAP);
		builder.with(AssemblyProperties.EAST_SAP);
	}

	@Override
	public boolean isSideSapSource(BlockState blockState, Facing side) {
		switch (side) {
			case NORTH:
				return blockState.get(AssemblyProperties.NORTH_SAP);
			case SOUTH:
				return blockState.get(AssemblyProperties.SOUTH_SAP);
			case WEST:
				return blockState.get(AssemblyProperties.WEST_SAP);
			case EAST:
				return blockState.get(AssemblyProperties.EAST_SAP);
			default:
				return false;
		}
	}
}
