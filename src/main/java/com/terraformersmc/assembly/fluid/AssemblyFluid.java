package com.terraformersmc.assembly.fluid;

import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.item.AssemblyItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Random;
import java.util.function.Consumer;

public abstract class AssemblyFluid extends FlowableFluid implements TexturedFluid {

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
	public Item getBucketItem() {
		return AssemblyItems.getBucket(this);
	}

	@Environment(EnvType.CLIENT)
	public void method_15776(World world, BlockPos pos, FluidState state, Random random) {
		if (!state.isStill() && !state.get(FALLING)) {
			if (random.nextInt(64) == 0) {
				world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} else if (random.nextInt(10) == 0) {
			world.addParticle(ParticleTypes.UNDERWATER, (double) ((float) pos.getX() + random.nextFloat()), (double) ((float) pos.getY() + random.nextFloat()), (double) ((float) pos.getZ() + random.nextFloat()), 0.0D, 0.0D, 0.0D);
		}

	}

	@Environment(EnvType.CLIENT)
	@Override
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean isInfinite() {
		return this.infinite;
	}

	@Override
	protected void beforeBreakingBlock(IWorld world, BlockPos pos, BlockState state) {
		BlockEntity var4 = state.getBlock().hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world.getWorld(), pos, var4);
	}

	@Override
	public int getFlowSpeed(WorldView world) {
		return 4;
	}

	@Override
	public BlockState toBlockState(FluidState state) {
		return AssemblyBlocks.getFluidBlock(this).getDefaultState().with(FluidBlock.LEVEL, method_15741(state));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == this || fluid == AssemblyFluids.getInverse(this);
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView world) {
		return this.levelDecreasePerBlock;
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && fluid != this && fluid != AssemblyFluids.getInverse(this);
	}

	@Override
	protected float getBlastResistance() {
		return this.blastResistance;
	}

	@Override
	public int getTickRate(WorldView var1) {
		return this.tickRate;
	}

	public static class Flowing extends AssemblyFluid {
		public Flowing(Settings settings) {
			super(settings);
		}

		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
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
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState state) {
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
		return new Identifier(Assembly.MOD_ID, "fluid/" + this.name + "_flowing");
	}

	@Override
	public Identifier getStillTexture() {
		return new Identifier(Assembly.MOD_ID, "fluid/" + this.name + "_still");
	}

	public static class Settings {
		private String name;
		private Identifier id;
		private int tickRate = 5;
		private int levelDecreasePerBlock = 1;
		private float blastResistance = 100.0F;
		private boolean infinite = false;
		Consumer<FluidKey.FluidKeyBuilder> fluidKeyBuilder = null;

		public Settings(String name) {
			this.name = name;
			this.id = new Identifier(Assembly.MOD_ID, name);
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

		public Settings fluidKey(Consumer<FluidKey.FluidKeyBuilder> builder) {
			this.fluidKeyBuilder = builder;
			return this;
		}

		public String getName() {
			return this.name;
		}

		public Identifier getId() {
			return this.id;
		}
	}
}
