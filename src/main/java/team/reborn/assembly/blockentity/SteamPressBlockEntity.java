package team.reborn.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.shape.VoxelShape;
import team.reborn.assembly.attributes.IOFluidContainer;
import team.reborn.assembly.attributes.SimpleIOFluidContainer;
import team.reborn.assembly.block.SteamPressBlock;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.util.AssemblyConstants;

public class SteamPressBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

	private static int MAX_PROGRESS = 5;
	private static int MAX_RESET = 30;
	private static final String PROGRESS_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int progress = 0;
	private static final String RESET_KEY = AssemblyConstants.NbtKeys.PRESS_RESET;
	private int reset = MAX_RESET;

	private static final FluidKey STEAM = FluidKeys.get(AssemblyFluids.STEAM);
	private static final FluidAmount STEAM_COST_PER_PROGRESS = FluidAmount.BUCKET.roundedDiv(100);
	private static final FluidAmount STEAM_COST_PER_RESET = FluidAmount.BUCKET.roundedDiv(100);

	private static final String INPUT_FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount INPUT_CAPACITY = FluidAmount.BUCKET.checkedMul(4);
	private final IOFluidContainer inputTank;

	private static final double LOWEST_ARM_OFFSET = -9 / 16F; // 9/16 because the lowest the arm should go is 9 pixels down.

	public SteamPressBlockEntity() {
		super(AssemblyBlockEntities.STEAM_PRESS);
		inputTank = new SimpleIOFluidContainer(1, INPUT_CAPACITY);
	}

	@Override
	public void tick() {
		MAX_PROGRESS = 5;
		MAX_RESET = 50;
		if (world != null) {
			if (!world.isClient && getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.LOWER) {
				if (reset < MAX_RESET) {
					if (!inputTank.attemptExtraction(STEAM::equals, STEAM_COST_PER_RESET, Simulation.SIMULATE).isEmpty()) {
						inputTank.attemptExtraction(STEAM::equals, STEAM_COST_PER_RESET, Simulation.ACTION);
						reset++;
					}
					if (reset == MAX_RESET) {
						progress = 0;
					}
					sync();
				} else {
					if (!inputTank.attemptExtraction(STEAM::equals, STEAM_COST_PER_PROGRESS, Simulation.SIMULATE).isEmpty()) {
						inputTank.attemptExtraction(STEAM::equals, STEAM_COST_PER_PROGRESS, Simulation.ACTION);
						progress++;
					}
					if (progress == MAX_PROGRESS) {
						world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.6F, 0.5F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
						if (world instanceof ServerWorld) {
							((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.GOLD_INGOT)), pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0.05);
						}
						reset = 0;
					}
					sync();
				}
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.contains(INPUT_FLUIDS_KEY)) {
			FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(INPUT_FLUIDS_KEY));
			this.inputTank.setInvFluid(0, fluid, Simulation.ACTION);
		}
		this.progress = tag.getInt(PROGRESS_KEY);
		this.reset = tag.getInt(RESET_KEY);

	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		FluidVolume inputFluid = this.inputTank.getInvFluid(0);
		if (!inputFluid.isEmpty()) {
			tag.put(INPUT_FLUIDS_KEY, inputFluid.toTag());
		}
		tag.putInt(PROGRESS_KEY, progress);
		tag.putInt(RESET_KEY, reset);
		return tag;
	}

	public VoxelShape getArmVoxelShape(DoubleBlockHalf half) {
		return SteamPressBlock.ARM_SHAPE.offset(0, half == DoubleBlockHalf.LOWER ? getArmOffset() + 1 : getArmOffset(), 0);
	}

	public IOFluidContainer getInputTank() {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			BlockEntity lowerHalf = world.getBlockEntity(pos.down());
			if (state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER && lowerHalf instanceof SteamPressBlockEntity) {
				return ((SteamPressBlockEntity) lowerHalf).inputTank;
			}
		}
		return inputTank;
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	private SteamPressBlockEntity getMaster() {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
				BlockEntity lowerHalf = world.getBlockEntity(pos.down());
				if (lowerHalf instanceof SteamPressBlockEntity) {
					return ((SteamPressBlockEntity) lowerHalf);
				}
			}
		}
		return this;
	}

	public double getArmOffset() {
		double lowestPosition = -LOWEST_ARM_OFFSET;
		if (getReset() != MAX_RESET) {
			// Reset-based animation (Up)
			return lowestPosition * Math.sin(Math.PI / 2.0 * ((double) getReset() / (double) MAX_RESET)) - lowestPosition;
		} else {
			// Progress-based animation (Down)
			return -lowestPosition * Math.sin(Math.PI / 2.0 * ((double) getProgress() / (double) MAX_PROGRESS) - Math.PI / 2.0) - lowestPosition;
		}
	}

	private int getProgress() {
		return getMaster().progress;
	}

	private int getReset() {
		return getMaster().reset;
	}
}
