package team.reborn.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.block.FluidHopperBlock;
import team.reborn.assembly.util.AssemblyConstants;
import team.reborn.assembly.util.fluid.IOFluidContainer;
import team.reborn.assembly.util.fluid.SimpleIOFluidContainer;
import team.reborn.assembly.util.interaction.interactable.TankIOInteractable;

import javax.annotation.Nullable;

public class FluidHopperBlockEntity extends BlockEntity implements FluidHopper, Tickable, Nameable, TankIOInteractable {
	private static final String CUSTOM_NAME_KEY = AssemblyConstants.NbtKeys.CUSTOM_NAME;
	private static final String TRANSFER_COOLDOWN_KEY = AssemblyConstants.NbtKeys.TRANSFER_COOLDOWN;
	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount CAPACITY = FluidAmount.BUCKET;
	private static final FluidAmount TRANSFER_AMOUNT = FluidAmount.BUCKET.roundedDiv(4);
	private final IOFluidContainer tank;

	private int transferCooldown;
	private Text customName;
	private long lastTickTime;

	public FluidHopperBlockEntity() {
		super(AssemblyBlockEntities.FLUID_HOPPER);
		tank = new SimpleIOFluidContainer(5, CAPACITY);
		this.transferCooldown = -1;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		if (tag.contains(FLUIDS_KEY)) {
			this.tank.fromTag(tag.getCompound(FLUIDS_KEY));
		}
		if (tag.contains(CUSTOM_NAME_KEY, 8)) {
			this.customName = Text.Serializer.fromJson(tag.getString(CUSTOM_NAME_KEY));
		}
		this.transferCooldown = tag.getInt(TRANSFER_COOLDOWN_KEY);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.put(FLUIDS_KEY, this.tank.toTag());
		tag.putInt(TRANSFER_COOLDOWN_KEY, this.transferCooldown);
		if (this.customName != null) {
			tag.putString(CUSTOM_NAME_KEY, Text.Serializer.toJson(this.customName));
		}
		return tag;
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient) {
			--this.transferCooldown;
			this.lastTickTime = this.world.getTime();
			if (!this.needsCooldown()) {
				boolean didWork = false;
				BlockPos upPos = pos.up();

				// Pull Fluid from fluid containers
				boolean pulled = !FluidVolumeUtil.move(FluidAttributes.EXTRACTABLE.get(world, upPos), tank, TRANSFER_AMOUNT).isEmpty();
				boolean pushed = !FluidVolumeUtil.move(tank, FluidAttributes.INSERTABLE.get(world, pos.offset(getCachedState().get(FluidHopperBlock.FACING))), TRANSFER_AMOUNT).isEmpty();
				if (pulled || pushed) {
					didWork = true;
				} else {
					// Pull fluid from BlockState
					BlockState upBlockState = world.getBlockState(upPos);
					if (upBlockState.getBlock() instanceof FluidDrainable) {
						Fluid stateFluid = upBlockState.getFluidState().getFluid();
						if (stateFluid != Fluids.EMPTY && tank.attemptInsertion(FluidKeys.get(stateFluid).withAmount(FluidAmount.BUCKET), Simulation.SIMULATE).isEmpty()) {
							Fluid fluid = ((FluidDrainable) upBlockState.getBlock()).tryDrainFluid(world, upPos, upBlockState);
							if (fluid != Fluids.EMPTY) {
								FluidVolume upFluidVolume = FluidKeys.get(fluid).withAmount(FluidAmount.BUCKET);
								if (tank.attemptInsertion(upFluidVolume, Simulation.SIMULATE).isEmpty()) {
									tank.attemptInsertion(upFluidVolume, Simulation.ACTION);
									world.playSound(null, upPos, fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
									didWork = true;
								}
							}
						}
					}
				}
				if (didWork) {
					this.setCooldown(8);
				}
			}
		}
	}

	@Override
	public double getHopperX() {
		return (double) this.pos.getX() + 0.5D;
	}

	@Override
	public double getHopperY() {
		return (double) this.pos.getY() + 0.5D;
	}

	@Override
	public double getHopperZ() {
		return (double) this.pos.getZ() + 0.5D;
	}

	private void setCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	private boolean needsCooldown() {
		return this.transferCooldown > 0;
	}

	private boolean isDisabled() {
		return this.transferCooldown > 8;
	}

	public void onEntityCollided(Entity entity) {
		//pull fluids out of item entities
	}

	@Override
	public IOFluidContainer getFluidTank() {
		return tank;
	}

	public void setCustomName(Text customName) {
		this.customName = customName;
	}

	@Override
	public Text getName() {
		return this.customName != null ? this.customName : this.getContainerName();
	}

	@Override
	public Text getDisplayName() {
		return this.getName();
	}

	@Override
	@Nullable
	public Text getCustomName() {
		return this.customName;
	}

	private Text getContainerName() {
		return AssemblyBlocks.FLUID_HOPPER.getName();
	}

	public IOFluidContainer getTank() {
		return tank;
	}

	@Override
	public FluidInsertable getInteractableInsertable() {
		return tank;
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		return tank;
	}
}
