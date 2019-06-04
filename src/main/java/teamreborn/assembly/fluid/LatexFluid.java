package teamreborn.assembly.fluid;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.registry.AssemblyBlocks;
import teamreborn.assembly.registry.AssemblyFluids;
import teamreborn.assembly.registry.AssemblyItems;
import teamreborn.assembly.tags.AssemblyFluidTags;

import java.util.Random;

public abstract class LatexFluid extends BaseFluid implements TexturedFluid {

	public LatexFluid() {
	}

	public Fluid getFlowing() {
		return AssemblyFluids.FLOWING_LATEX;
	}

	public Fluid getStill() {
		return AssemblyFluids.LATEX;
	}

	@Environment(EnvType.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	public Item getBucketItem() {
		return AssemblyItems.LATEX_BUCKET;
	}

	@Environment(EnvType.CLIENT)
	public void method_15776(World var1, BlockPos var2, FluidState var3, Random var4) {
		if (!var3.isStill() && !var3.get(FALLING)) {
			if (var4.nextInt(64) == 0) {
				var1.playSound((double) var2.getX() + 0.5D, (double) var2.getY() + 0.5D, (double) var2.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, var4.nextFloat() * 0.25F + 0.75F, var4.nextFloat() + 0.5F, false);
			}
		} else if (var4.nextInt(10) == 0) {
			var1.addParticle(ParticleTypes.UNDERWATER, (double) ((float) var2.getX() + var4.nextFloat()), (double) ((float) var2.getY() + var4.nextFloat()), (double) ((float) var2.getZ() + var4.nextFloat()), 0.0D, 0.0D, 0.0D);
		}

	}

	@Environment(EnvType.CLIENT)
	public ParticleType method_15787() {
		return ParticleTypes.DRIPPING_WATER;
	}

	protected boolean isInfinite() {
		return true;
	}

	protected void beforeBreakingBlock(IWorld var1, BlockPos var2, BlockState var3) {
		BlockEntity var4 = var3.getBlock().hasBlockEntity() ? var1.getBlockEntity(var2) : null;
		Block.dropStacks(var3, var1.getWorld(), var2, var4);
	}

	public int method_15733(ViewableWorld var1) {
		return 4;
	}

	public BlockState toBlockState(FluidState var1) {
		return AssemblyBlocks.LATEX.getDefaultState().with(FluidBlock.LEVEL, method_15741(var1));
	}

	public boolean matchesType(Fluid var1) {
		return var1 == AssemblyFluids.LATEX || var1 == AssemblyFluids.FLOWING_LATEX;
	}

	public int getLevelDecreasePerBlock(ViewableWorld var1) {
		return 1;
	}

	public int method_15789(ViewableWorld var1) {
		return 5;
	}

	public boolean method_15777(FluidState var1, Fluid var2, Direction var3) {
		return var3 == Direction.DOWN && !var2.matches(AssemblyFluidTags.LATEX);
	}

	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class Flowing extends LatexFluid {
		public Flowing() {
		}

		@Override
		protected void appendProperties(StateFactory.Builder<Fluid, FluidState> var1) {
			super.appendProperties(var1);
			var1.add(LEVEL);
		}

		@Override
		protected boolean method_15777(FluidState var1, BlockView var2, BlockPos var3, Fluid var4, Direction var5) {
			return false;
		}

		@Override
		public int getTickRate(ViewableWorld var1) {
			return 15;
		}

		@Override
		public int getLevel(FluidState var1) {
			return var1.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState var1) {
			return false;
		}
	}

	public static class Still extends LatexFluid {
		public Still() {
		}

		@Override
		public int getLevel(FluidState var1) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState var1) {
			return true;
		}

		@Override
		protected boolean method_15777(FluidState var1, BlockView var2, BlockPos var3, Fluid var4, Direction var5) {
			return false;
		}

		@Override
		public int getTickRate(ViewableWorld var1) {
			return 15;
		}
	}

	@Override
	public Identifier getFlowingTexture() {
		return new Identifier(Assembly.MOD_ID, "fluid/latex_flowing");
	}

	@Override
	public Identifier getStillTexture() {
		return new Identifier(Assembly.MOD_ID, "fluid/latex_still");
	}
}
