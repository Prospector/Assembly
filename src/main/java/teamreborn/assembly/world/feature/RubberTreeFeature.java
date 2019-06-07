package teamreborn.assembly.world.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.OakTreeFeature;
import teamreborn.assembly.block.AssemblyBlocks;
import teamreborn.assembly.util.block.AssemblyProperties;

import java.util.function.Function;

public class RubberTreeFeature extends OakTreeFeature {
	public RubberTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function_1,
	                         boolean boolean_1) {
		this(function_1, boolean_1, 7, AssemblyBlocks.RUBBER_LOG.getDefaultState().with(AssemblyProperties.ALIVE, true), AssemblyBlocks.RUBBER_LEAVES.getDefaultState(), false);
	}

	public RubberTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function_1, boolean boolean_1, int int_1, BlockState log, BlockState leaves, boolean boolean_2) {
		super(function_1, boolean_1, int_1, log, leaves, boolean_2);
	}
}
