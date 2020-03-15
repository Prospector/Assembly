package team.reborn.assembly.mixintf;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import team.reborn.assembly.util.AssemblyConstants;

import javax.annotation.Nullable;

public interface DrillableOre {
	BooleanProperty PLACED = AssemblyConstants.Properties.PLACED;

	@Nullable
	default BlockState modifyPlacementState(BlockState state) {
		return state == null ? null : state.with(PLACED, true);
	}

	default StateManager.Builder<Block, BlockState> appendProperty(StateManager.Builder<Block, BlockState> builder) {
		return builder.add(PLACED);
	}
}
