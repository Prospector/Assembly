package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.BiomeSourcedFluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.block.FluidHopperBlock;
import com.terraformersmc.assembly.screen.builder.ScreenHandlerBuilder;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.screen.builder.ScreenSyncerProvider;
import com.terraformersmc.assembly.screen.builder.TankStyle;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankIOInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
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

import javax.annotation.Nullable;

public class FluidHopperBlockEntity extends BlockEntity implements FluidHopper, Tickable, Nameable, TankIOInteractable, ScreenSyncerProvider<FluidHopperBlockEntity> {
	private static final String CUSTOM_NAME_KEY = AssemblyConstants.NbtKeys.CUSTOM_NAME;
	private static final String TRANSFER_COOLDOWN_KEY = AssemblyConstants.NbtKeys.TRANSFER_COOLDOWN;
	private static final String WORLD_PULL_COOLDOWN_KEY = AssemblyConstants.NbtKeys.WORLD_PULL_COOLDOWN;
	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount CAPACITY = FluidAmount.BUCKET;
	private static final int BUCKET_TRANSFER_DIVISOR = 4;
	private static final FluidAmount TRANSFER_AMOUNT = FluidAmount.BUCKET.roundedDiv(BUCKET_TRANSFER_DIVISOR);
	private final IOFluidContainer tank;

	private int transferCooldown;
	private int pullFromWorldCooldown;
	private Text customName;

	public FluidHopperBlockEntity() {
		super(AssemblyBlockEntities.FLUID_HOPPER);
		this.tank = new SimpleIOFluidContainer(5, CAPACITY);
		this.tank.addListener((inv, tank1, previous, current) -> markDirty(), () -> {
		});
		this.transferCooldown = -1;
		this.pullFromWorldCooldown = -1;
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
		this.pullFromWorldCooldown = tag.getInt(WORLD_PULL_COOLDOWN_KEY);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.put(FLUIDS_KEY, this.tank.toTag());
		tag.putInt(TRANSFER_COOLDOWN_KEY, this.transferCooldown);
		tag.putInt(WORLD_PULL_COOLDOWN_KEY, this.pullFromWorldCooldown);
		if (this.customName != null) {
			tag.putString(CUSTOM_NAME_KEY, Text.Serializer.toJson(this.customName));
		}
		return tag;
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isClient) {
			boolean pulledFromContainer = false;

			if (!canTransfer()) {
				--this.transferCooldown;
			}
			if (!canPullFromWorld()) {
				--this.pullFromWorldCooldown;
			}


			if (this.getCachedState().get(HopperBlock.ENABLED)) {
				// Try to transfer to/from containers
				if (this.canTransfer()) {
					BlockPos upPos = this.pos.up();
					boolean pulled = !FluidVolumeUtil.move(FluidAttributes.EXTRACTABLE.get(this.world, upPos), this.tank, TRANSFER_AMOUNT).isEmpty();
					boolean pushed = !FluidVolumeUtil.move(this.tank, FluidAttributes.INSERTABLE.get(this.world, this.pos.offset(this.getCachedState().get(FluidHopperBlock.FACING))), TRANSFER_AMOUNT).isEmpty();
					if (pulled || pushed) {
						this.resetTransferCooldown();
						this.markDirty();
					}
					pulledFromContainer = pulled;
				}

				// If it didn't pull from a container, try to pull from the world
				if (!pulledFromContainer && canPullFromWorld()) {
					BlockPos upPos = this.pos.up();
					BlockState upBlockState = this.world.getBlockState(upPos);
					if (upBlockState.getBlock() instanceof FluidDrainable) {
						Fluid stateFluid = upBlockState.getFluidState().getFluid();
						if (stateFluid != Fluids.EMPTY && this.tank.attemptInsertion(FluidKeys.get(stateFluid).withAmount(FluidAmount.BUCKET), Simulation.SIMULATE).isEmpty()) {
							Fluid fluid = ((FluidDrainable) upBlockState.getBlock()).tryDrainFluid(this.world, upPos, upBlockState);
							if (fluid != Fluids.EMPTY) {
								FluidKey key = FluidKeys.get(fluid);
								FluidVolume upFluidVolume = key instanceof BiomeSourcedFluidKey ? ((BiomeSourcedFluidKey) key).withAmount(world.getBiome(pos), FluidAmount.BUCKET) : key.withAmount(FluidAmount.BUCKET);
								if (this.tank.attemptInsertion(upFluidVolume, Simulation.SIMULATE).isEmpty()) {
									this.tank.attemptInsertion(upFluidVolume, Simulation.ACTION);
									this.world.playSound(null, upPos, fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
									this.resetWorldCooldown();
									this.markDirty();
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public ScreenSyncer<FluidHopperBlockEntity> createSyncer(int syncId, PlayerInventory inventory) {
		return new ScreenHandlerBuilder(AssemblyConstants.Ids.FLUID_HOPPER, 176, 156, this)
				.player(inventory.player)
				.inventory()
				.hotbar()
				.addInventory()

				.container(this)
				.tank(29, 17, TankStyle.ONE, tank, 0)
				.tank(53, 17, TankStyle.ONE, tank, 1)
				.tank(77, 17, TankStyle.ONE, tank, 2)
				.tank(101, 17, TankStyle.ONE, tank, 3)
				.tank(125, 17, TankStyle.ONE, tank, 4)
				.addContainer()

				.create(this, syncId);
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

	private void resetTransferCooldown() {
		this.transferCooldown = 8;
	}

	private void resetWorldCooldown() {
		this.pullFromWorldCooldown = 8 * BUCKET_TRANSFER_DIVISOR;
	}

	private boolean canTransfer() {
		return this.transferCooldown <= 0;
	}

	private boolean canPullFromWorld() {
		return this.pullFromWorldCooldown <= 0;
	}

	private boolean isDisabled() {
		return this.transferCooldown > 8 && !world.getBlockState(pos).get(FluidHopperBlock.ENABLED);
	}

	public void onEntityCollided(Entity entity) {
		//pull fluids out of item entities
	}

	@Override
	public IOFluidContainer getFluidTank() {
		return this.tank;
	}

	public void setCustomName(Text customName) {
		this.customName = customName;
	}

	@Override
	public Text getName() {
		return this.customName != null ? this.customName : AssemblyBlocks.FLUID_HOPPER.getName();
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

	@Override
	public FluidInsertable getInteractableInsertable() {
		return this.tank;
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		return this.tank;
	}
}
