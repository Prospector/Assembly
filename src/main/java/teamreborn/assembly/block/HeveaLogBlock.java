package teamreborn.assembly.block;

import com.terraformersmc.terraform.block.StrippableLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import teamreborn.assembly.api.SapSource;
import teamreborn.assembly.util.block.AssemblyProperties;

import java.util.Random;
import java.util.function.Supplier;

public class HeveaLogBlock extends StrippableLogBlock implements SapSource {

	public HeveaLogBlock(Supplier<Block> stripped, MaterialColor materialColor, Settings settings) {
		super(stripped, materialColor, settings);
		setDefaultState(this.getDefaultState()
			.with(AssemblyProperties.ALIVE, false)
			.with(AssemblyProperties.NORTH_LATEX, false)
			.with(AssemblyProperties.SOUTH_LATEX, false)
			.with(AssemblyProperties.WEST_LATEX, false)
			.with(AssemblyProperties.EAST_LATEX, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(AssemblyProperties.ALIVE);
		builder.add(AssemblyProperties.NORTH_LATEX);
		builder.add(AssemblyProperties.SOUTH_LATEX);
		builder.add(AssemblyProperties.WEST_LATEX);
		builder.add(AssemblyProperties.EAST_LATEX);
	}

	@Override
	public boolean isSideSapSource(WorldView world, BlockPos pos, BlockState state, Direction side) {
		switch (side) {
			case NORTH:
				return state.get(AssemblyProperties.NORTH_LATEX);
			case SOUTH:
				return state.get(AssemblyProperties.SOUTH_LATEX);
			case WEST:
				return state.get(AssemblyProperties.WEST_LATEX);
			case EAST:
				return state.get(AssemblyProperties.EAST_LATEX);
			default:
				return false;
		}
	}

	public static BooleanProperty getRandomLatexProperty(Random random) {
		int side = random.nextInt(4);
		if (side == 0) {
			return AssemblyProperties.NORTH_LATEX;
		} else if (side == 1) {
			return AssemblyProperties.EAST_LATEX;
		} else if (side == 2) {
			return AssemblyProperties.SOUTH_LATEX;
		} else {
			return AssemblyProperties.WEST_LATEX;
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(AssemblyProperties.ALIVE) && !(state.get(AssemblyProperties.NORTH_LATEX) || state.get(AssemblyProperties.SOUTH_LATEX) || state.get(AssemblyProperties.EAST_LATEX) || state.get(AssemblyProperties.WEST_LATEX));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(40) == 0) {
			if (random.nextInt(5) == 0) {
				world.setBlockState(pos, state.with(AssemblyProperties.ALIVE, false));
			} else {
				world.setBlockState(pos, state.with(getRandomLatexProperty(random), true));
			}
		}
	}
}
