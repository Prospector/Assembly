package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.block.MaterialColor;
import net.minecraft.state.StateFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import teamreborn.assembly.api.SapSource;
import teamreborn.assembly.util.block.AssemblyProperties;

public class RubberLogBlock extends LogBlock implements SapSource {

	public RubberLogBlock(MaterialColor materialColor, Settings settings) {
		super(materialColor, settings);
		setDefaultState(this.getDefaultState()
			.with(AssemblyProperties.ALIVE, false)
			.with(AssemblyProperties.NORTH_SAP, false)
			.with(AssemblyProperties.SOUTH_SAP, false)
			.with(AssemblyProperties.WEST_SAP, false)
			.with(AssemblyProperties.EAST_SAP, false));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(AssemblyProperties.ALIVE);
		builder.add(AssemblyProperties.NORTH_SAP);
		builder.add(AssemblyProperties.SOUTH_SAP);
		builder.add(AssemblyProperties.WEST_SAP);
		builder.add(AssemblyProperties.EAST_SAP);
	}

	@Override
	public boolean isSideSapSource(ViewableWorld world, BlockPos pos, BlockState state, Direction side) {
		switch (side) {
			case NORTH:
				return state.get(AssemblyProperties.NORTH_SAP);
			case SOUTH:
				return state.get(AssemblyProperties.SOUTH_SAP);
			case WEST:
				return state.get(AssemblyProperties.WEST_SAP);
			case EAST:
				return state.get(AssemblyProperties.EAST_SAP);
			default:
				return false;
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(AssemblyProperties.ALIVE) && !(state.get(AssemblyProperties.NORTH_SAP) || state.get(AssemblyProperties.SOUTH_SAP) || state.get(AssemblyProperties.EAST_SAP) || state.get(AssemblyProperties.WEST_SAP));
	}
}
