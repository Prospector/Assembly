package teamreborn.assembly.fluid;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import teamreborn.assembly.registry.AssemblyFluids;
import teamreborn.assembly.registry.AssemblyItems;

import java.util.Random;

public abstract class SapFluid extends BaseFluid {

	public SapFluid() {
	}

	public Fluid getFlowing() {
		return AssemblyFluids.LATEX_FLOWING;
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
		if (!var3.isStill() && !var3.get(STILL)) {
			if (var4.nextInt(64) == 0) {
				var1.playSound((double) var2.getX() + 0.5D, (double) var2.getY() + 0.5D, (double) var2.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCK, var4.nextFloat() * 0.25F + 0.75F, var4.nextFloat() + 0.5F, false);
			}
		} else if (var4.nextInt(10) == 0) {
			var1.addParticle(ParticleTypes.UNDERWATER, (double) ((float) var2.getX() + var4.nextFloat()), (double) ((float) var2.getY() + var4.nextFloat()), (double) ((float) var2.getZ() + var4.nextFloat()), 0.0D, 0.0D, 0.0D);
		}

	}

	@Environment(EnvType.CLIENT)
	public ParticleType method_15787() {
		return ParticleTypes.DRIPPING_WATER;
	}

	protected boolean method_15737() {
		return true;
	}

	protected void method_15730(IWorld var1, BlockPos var2, BlockState var3) {
		BlockEntity var4 = var3.getBlock().hasBlockEntity() ? var1.getBlockEntity(var2) : null;
		Block.dropStacks(var3, var1.getWorld(), var2, var4);
	}

	public int method_15733(ViewableWorld var1) {
		return 4;
	}

	public BlockState toBlockState(FluidState var1) {
		return Blocks.WATER.getDefaultState().with(FluidBlock.field_11278, method_15741(var1));
	}

	public boolean matchesType(Fluid var1) {
		return var1 == Fluids.WATER || var1 == Fluids.FLOWING_WATER;
	}

	public int method_15739(ViewableWorld var1) {
		return 1;
	}

	public int method_15789(ViewableWorld var1) {
		return 5;
	}

	public boolean method_15777(FluidState var1, Fluid var2, Direction var3) {
		return var3 == Direction.DOWN && !var2.matches(FluidTags.WATER);
	}

	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class Flowing extends BiomassFluid {
		public Flowing() {
		}

		@Override
		protected void appendProperties(StateFactory.Builder<Fluid, FluidState> var1) {
			super.appendProperties(var1);
			var1.with(LEVEL);
		}

		@Override
		public int method_15779(FluidState var1) {
			return var1.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState var1) {
			return false;
		}
	}

	public static class Still extends BiomassFluid {
		public Still() {
		}

		@Override
		public int method_15779(FluidState var1) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState var1) {
			return true;
		}
	}
}
