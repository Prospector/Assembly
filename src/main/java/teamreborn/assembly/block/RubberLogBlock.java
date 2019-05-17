package teamreborn.assembly.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LogBlock;
import net.minecraft.block.MaterialColor;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import teamreborn.assembly.api.SapSource;
import teamreborn.assembly.util.block.AssemblyProperties;

public class RubberLogBlock extends LogBlock implements SapSource {
	public static final BooleanProperty NORTH_SAP = AssemblyProperties.NORTH_SAP;
	public static final BooleanProperty SOUTH_SAP = AssemblyProperties.SOUTH_SAP;
	public static final BooleanProperty WEST_SAP = AssemblyProperties.WEST_SAP;
	public static final BooleanProperty EAST_SAP = AssemblyProperties.EAST_SAP;
	public static final BooleanProperty POURING = AssemblyProperties.POURING;

	public RubberLogBlock(MaterialColor materialColor, Settings settings) {
		super(materialColor, settings);
		setDefaultState(this.getDefaultState()
			.with(AssemblyProperties.NORTH_SAP, false)
			.with(AssemblyProperties.SOUTH_SAP, false)
			.with(AssemblyProperties.WEST_SAP, false)
			.with(AssemblyProperties.EAST_SAP, false));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(AssemblyProperties.NORTH_SAP);
		builder.add(AssemblyProperties.SOUTH_SAP);
		builder.add(AssemblyProperties.WEST_SAP);
		builder.add(AssemblyProperties.EAST_SAP);
	}

	@Override
	public boolean isSideSapSource(ViewableWorld world, BlockPos pos, BlockState blockState, Direction side) {
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
