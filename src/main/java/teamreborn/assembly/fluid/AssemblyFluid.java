package teamreborn.assembly.fluid;

import teamreborn.assembly.Assembly;
import teamreborn.assembly.block.AssemblyBlocks;
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
import net.minecraft.particle.ParticleEffect;
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
import teamreborn.assembly.item.AssemblyItems;
import teamreborn.assembly.tags.AssemblyFluidTags;

import java.util.Random;

public abstract class AssemblyFluid extends BaseFluid implements TexturedFluid {

	public final String name;
	public final int tickRate;
	public final int levelDecreasePerBlock;
	public final float blastResistance;
	public final boolean infinite;

	public AssemblyFluid(Settings settings) {
		this.name = settings.name;
		this.tickRate = settings.tickRate;
		this.levelDecreasePerBlock = settings.levelDecreasePerBlock;
		this.blastResistance = settings.blastResistance;
		this.infinite = settings.infinite;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public Item getBucketItem() {
		return AssemblyItems.getBucket(this);
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
	@Override
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean isInfinite() {
		return infinite;
	}

	@Override
	protected void beforeBreakingBlock(IWorld var1, BlockPos var2, BlockState var3) {
		BlockEntity var4 = var3.getBlock().hasBlockEntity() ? var1.getBlockEntity(var2) : null;
		Block.dropStacks(var3, var1.getWorld(), var2, var4);
	}

	@Override
	public int method_15733(ViewableWorld var1) {
		return 4;
	}

	@Override
	public BlockState toBlockState(FluidState var1) {
		return AssemblyBlocks.getFluidBlock(this).getDefaultState().with(FluidBlock.LEVEL, method_15741(var1));
	}

	@Override
	public boolean matchesType(Fluid var1) {
		return var1 == this || var1 == AssemblyFluids.getInverse(this);
	}

	@Override
	public int getLevelDecreasePerBlock(ViewableWorld var1) {
		return levelDecreasePerBlock;
	}

	@Override
	protected boolean method_15777(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.matches(AssemblyFluidTags.get(this));
	}

	@Override
	protected float getBlastResistance() {
		return blastResistance;
	}

	@Override
	public int getTickRate(ViewableWorld var1) {
		return tickRate;
	}

	public static class Flowing extends AssemblyFluid {
		public Flowing(Settings settings) {
			super(settings);
		}

		@Override
		protected void appendProperties(StateFactory.Builder<Fluid, FluidState> var1) {
			super.appendProperties(var1);
			var1.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState var1) {
			return var1.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState var1) {
			return false;
		}

		@Override
		public Fluid getFlowing() {
			return this;
		}

		@Override
		public Fluid getStill() {
			return AssemblyFluids.getStill(this);
		}
	}

	public static class Still extends AssemblyFluid {
		public Still(Settings settings) {
			super(settings);
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
		public Fluid getFlowing() {
			return AssemblyFluids.getFlowing(this);
		}

		@Override
		public Fluid getStill() {
			return this;
		}
	}

	@Override
	public Identifier getFlowingTexture() {
		return new Identifier(Assembly.MOD_ID, "fluid/" + name + "_flowing");
	}

	@Override
	public Identifier getStillTexture() {
		return new Identifier(Assembly.MOD_ID, "fluid/" + name + "_still");
	}

	public static class Settings {
		private String name;
		private int tickRate = 5;
		private int levelDecreasePerBlock = 1;
		private float blastResistance = 100.0F;
		private boolean infinite = false;

		public Settings(String name) {
			this.name = name;
		}

		public Settings tickRate(int tickRate) {
			this.tickRate = tickRate;
			return this;
		}

		public Settings levelDecreasePerBlock(int levelDecreasePerBlock) {
			this.levelDecreasePerBlock = levelDecreasePerBlock;
			return this;
		}

		public Settings blastResistance(int blastResistance) {
			this.blastResistance = blastResistance;
			return this;
		}

		public Settings infinite() {
			this.infinite = true;
			return this;
		}

		public String getName() {
			return name;
		}
	}
}