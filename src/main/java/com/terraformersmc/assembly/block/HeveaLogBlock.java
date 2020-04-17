package com.terraformersmc.assembly.block;

import com.terraformersmc.assembly.api.SapSource;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.terraform.block.StrippableLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import java.util.Random;
import java.util.function.Supplier;

public class HeveaLogBlock extends StrippableLogBlock implements SapSource {

	public static final BooleanProperty ALIVE = AssemblyConstants.Properties.ALIVE;
	public static final BooleanProperty NORTH_LATEX = AssemblyConstants.Properties.NORTH_LATEX;
	public static final BooleanProperty EAST_LATEX = AssemblyConstants.Properties.EAST_LATEX;
	public static final BooleanProperty SOUTH_LATEX = AssemblyConstants.Properties.SOUTH_LATEX;
	public static final BooleanProperty WEST_LATEX = AssemblyConstants.Properties.WEST_LATEX;

	public HeveaLogBlock(Supplier<Block> stripped, MaterialColor materialColor, Settings settings) {
		super(stripped, materialColor, settings);
		setDefaultState(this.getDefaultState()
			.with(ALIVE, false)
			.with(NORTH_LATEX, false)
			.with(SOUTH_LATEX, false)
			.with(WEST_LATEX, false)
			.with(EAST_LATEX, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ALIVE);
		builder.add(NORTH_LATEX);
		builder.add(SOUTH_LATEX);
		builder.add(WEST_LATEX);
		builder.add(EAST_LATEX);
	}

	@Override
	public boolean isSideSapSource(WorldView world, BlockPos pos, BlockState state, Direction side) {
		switch (side) {
			case NORTH:
				return state.get(NORTH_LATEX);
			case SOUTH:
				return state.get(SOUTH_LATEX);
			case WEST:
				return state.get(WEST_LATEX);
			case EAST:
				return state.get(EAST_LATEX);
			default:
				return false;
		}
	}

	public static BooleanProperty getRandomLatexProperty(Random random) {
		int side = random.nextInt(4);
		if (side == 0) {
			return NORTH_LATEX;
		} else if (side == 1) {
			return EAST_LATEX;
		} else if (side == 2) {
			return SOUTH_LATEX;
		} else {
			return WEST_LATEX;
		}
	}

	public static BooleanProperty getLatexProperty(Direction direction) {
		switch (direction) {
			case NORTH:
				return NORTH_LATEX;
			case SOUTH:
				return SOUTH_LATEX;
			case EAST:
				return EAST_LATEX;
			case WEST:
				return WEST_LATEX;
			default:
				return null;
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(ALIVE) && !(state.get(NORTH_LATEX) || state.get(SOUTH_LATEX) || state.get(EAST_LATEX) || state.get(WEST_LATEX));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(40) == 0) {
			if (random.nextInt(5) == 0) {
				world.setBlockState(pos, state.with(ALIVE, false));
			} else {
				world.setBlockState(pos, state.with(getRandomLatexProperty(random), true));
			}
		}
	}
}
