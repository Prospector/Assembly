package team.reborn.assembly.blockentity;

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.registry.Registry;
import team.reborn.assembly.block.SpigotBlock;
import team.reborn.assembly.blockentity.base.AssemblySyncedNbtBlockEntity;
import team.reborn.assembly.util.AssemblyConstants;

public class SpigotBlockEntity extends AssemblySyncedNbtBlockEntity implements Tickable {

	private static final String TRANSFER_COOLDOWN_KEY = AssemblyConstants.NbtKeys.TRANSFER_COOLDOWN;
	private int transferCooldown;
	private long lastTickTime;

	private static final String POURING_FLUID_KEY = AssemblyConstants.NbtKeys.POURING_FLUID;
	private Fluid pouringFluid = Fluids.EMPTY;
	private Fluid lastPouringFluid = Fluids.EMPTY;

	public SpigotBlockEntity() {
		super(AssemblyBlockEntities.SPIGOT);
		this.transferCooldown = -1;
	}

	@Override
    public void fromTag(CompoundTag tag, boolean syncing) {
		this.transferCooldown = tag.getInt(TRANSFER_COOLDOWN_KEY);
		if (syncing) {
			this.pouringFluid = Registry.FLUID.get(new Identifier(tag.getString(POURING_FLUID_KEY)));
		}
	}

	@Override
    public CompoundTag toTag(CompoundTag tag, boolean syncing) {
		tag.putInt(TRANSFER_COOLDOWN_KEY, transferCooldown);
		if (syncing) {
			tag.putString(POURING_FLUID_KEY, Registry.FLUID.getId(pouringFluid).toString());
		}
		return tag;
	}

	@Override
    public void tick() {
		if (this.world != null && !this.world.isClient) {
			if (transferCooldown > 0) {
				--this.transferCooldown;
			}
			this.lastTickTime = this.world.getTime();
			if (!this.needsCooldown()) {
				if (world.getBlockState(pos).get(SpigotBlock.VALVE).isOpen()) {
					// Pull Fluid from fluid container it is attached to and push to the block below
					FluidVolume movedFluid = FluidVolumeUtil.move(FluidAttributes.EXTRACTABLE.get(world, pos.offset(getCachedState().get(SpigotBlock.FACING))), FluidAttributes.INSERTABLE.get(world, pos.down()), FluidAmount.BUCKET.roundedDiv(4));
					Fluid rawFluid = movedFluid.getRawFluid();
					this.pouringFluid = rawFluid == null ? Fluids.EMPTY : rawFluid;
					if (pouringFluid != lastPouringFluid) {
						world.setBlockState(pos, world.getBlockState(pos).with(SpigotBlock.POURING, pouringFluid != Fluids.EMPTY));
						sync();
					}
					if (!movedFluid.isEmpty()) {
						this.setCooldown(8);
					}
				} else {
					pouringFluid = Fluids.EMPTY;
					if (lastPouringFluid != pouringFluid) {
						world.setBlockState(pos, world.getBlockState(pos).with(SpigotBlock.POURING, false));
					}
				}
				lastPouringFluid = pouringFluid;
			}

		}
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

	public Fluid getPouringFluid() {
		return pouringFluid;
	}
}
