package teamreborn.assembly.world.feature;

import com.mojang.datafixers.Dynamic;
import teamreborn.assembly.block.AssemblyBlocks;
import teamreborn.assembly.block.HeveaLogBlock;
import teamreborn.assembly.util.block.AssemblyProperties;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class HeveaTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	public static final BlockState LOG = AssemblyBlocks.HEVEA_LOG.getDefaultState().with(AssemblyProperties.ALIVE, true);
	public static final BlockState LEAVES = AssemblyBlocks.HEVEA_LEAVES.getDefaultState();

	public HeveaTreeFeature(Function<Dynamic<?>, DefaultFeatureConfig> function, boolean worldGen) {
		super(function, worldGen);
	}

	@Override
	protected boolean generate(Set feature, ModifiableTestableWorld world, Random random, BlockPos pos, MutableIntBoundingBox box) {
		int height = this.getTreeHeight(random);
		boolean generate = true;
		if (pos.getY() >= 1 && pos.getY() + height + 1 <= 256) {
			int int_9;
			int int_18;
			for (int int_2 = pos.getY(); int_2 <= pos.getY() + 1 + height; ++int_2) {
				int int_3 = 1;
				if (int_2 == pos.getY()) {
					int_3 = 0;
				}

				if (int_2 >= pos.getY() + 1 + height - 2) {
					int_3 = 2;
				}

				BlockPos.Mutable blockPos$Mutable_1 = new BlockPos.Mutable();

				for (int_9 = pos.getX() - int_3; int_9 <= pos.getX() + int_3 && generate; ++int_9) {
					for (int_18 = pos.getZ() - int_3; int_18 <= pos.getZ() + int_3 && generate; ++int_18) {
						if (int_2 >= 0 && int_2 < 256) {
							if (!canTreeReplace(world, blockPos$Mutable_1.set(int_9, int_2, int_18))) {
								generate = false;
							}
						} else {
							generate = false;
						}
					}
				}
			}

			if (!generate) {
				return false;
			} else if (isDirtOrGrass(world, pos.down()) && pos.getY() < 256 - height - 1) {
				this.setToDirt(world, pos.down());
				int int_19;
				int z;
				BlockPos leavesPos;
				int y;
				for (y = pos.getY() - 3 + height; y <= pos.getY() + height; ++y) {
					int_9 = y - (pos.getY() + height);
					int_18 = 1 - int_9 / 2;

					for (int x = pos.getX() - int_18; x <= pos.getX() + int_18; ++x) {
						int_19 = x - pos.getX();

						for (z = pos.getZ() - int_18; z <= pos.getZ() + int_18; ++z) {
							int int_14 = z - pos.getZ();
							if (Math.abs(int_19) != int_18 || Math.abs(int_14) != int_18 || random.nextInt(2) != 0 && int_9 != 0) {
								leavesPos = new BlockPos(x, y, z);
								if (isAirOrLeaves(world, leavesPos) || isReplaceablePlant(world, leavesPos)) {
									this.setBlockState(feature, world, leavesPos, LEAVES, box);
								}
							}
						}
					}
				}

				for (y = 0; y < height; ++y) {
					if (isAirOrLeaves(world, pos.up(y)) || isReplaceablePlant(world, pos.up(y))) {
						if (random.nextInt(5) == 0) {
							this.setBlockState(feature, world, pos.up(y), LOG.with(HeveaLogBlock.getRandomLatexProperty(random), true), box);
						} else {
							this.setBlockState(feature, world, pos.up(y), LOG, box);
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	protected int getTreeHeight(Random random) {
		return 7 + random.nextInt(3);
	}
}
