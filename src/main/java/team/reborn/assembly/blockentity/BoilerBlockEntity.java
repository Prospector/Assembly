package team.reborn.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import team.reborn.assembly.attributes.IOFluidContainer;
import team.reborn.assembly.attributes.SimpleIOFluidContainer;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.block.BoilerBlock;
import team.reborn.assembly.container.builder.MenuBuilder;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.util.AssemblyConstants;

import java.util.HashMap;
import java.util.Map;

public class BoilerBlockEntity extends AssemblyContainerBlockEntity implements Tickable {
	private static final String BURN_TIME_KEY = AssemblyConstants.NbtKeys.BURN_TIME;
	private int burnTime;
	private int fuelTime;

	private Map<BlockPos, BlockEntity> chambers = new HashMap<>();

	private static final String INPUT_FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount INPUT_CAPACITY = FluidAmount.BUCKET.checkedMul(4);
	private final IOFluidContainer inputTank;

	private static final String OUTPUT_FLUIDS_KEY = AssemblyConstants.NbtKeys.OUTPUT_FLUIDS;
	private static final FluidAmount OUTPUT_CAPACITY_PER_CHAMBER = FluidAmount.BUCKET.checkedMul(4);
	private final IOFluidContainer outputTank;
	private FluidAmount outputCapacity = FluidAmount.ZERO;

	public BoilerBlockEntity() {
		super(AssemblyBlockEntities.BOILER);
		inputTank = new SimpleIOFluidContainer(1, INPUT_CAPACITY);
		outputTank = new SimpleIOFluidContainer(1, () -> outputCapacity);
	}

	@Override
	public void tick() {
		boolean wasBurning = this.isBurning();
		boolean dirty = false;
		if (this.isBurning()) {
			--this.burnTime;
			FluidVolume input = inputTank.getInvFluid(0);
			FluidVolume output = outputTank.getInvFluid(0);
			if (input.getRawFluid() == Fluids.WATER && output.getRawFluid() == Fluids.EMPTY || output.getRawFluid() == AssemblyFluids.STEAM && output.getAmount_F().isLessThan(outputTank.getCapacity(0))) {
				FluidAmount amount = inputTank.attemptAnyExtraction(FluidAmount.BUCKET.roundedDiv(1000), Simulation.ACTION).getAmount_F();
				outputTank.attemptInsertion(FluidKeys.get(AssemblyFluids.STEAM).withAmount(amount), Simulation.ACTION);
			}
		}

		chambers.clear();
		for (int i = 1; i < 255; i++) {
			BlockPos chamberPos = pos.offset(Direction.UP, i);
			BlockEntity chamber = world.getBlockEntity(chamberPos);
			if (chamber instanceof BoilerChamberBlockEntity) {
				chambers.put(chamberPos, chamber);
				((BoilerChamberBlockEntity) chamber).updateBoiler(pos);
			} else {
				outputCapacity = OUTPUT_CAPACITY_PER_CHAMBER.checkedMul(chambers.size());
				break;
			}
		}

		if (!this.world.isClient) {
			ItemStack fuelStack = this.contents.get(0);
			if (!this.isBurning() && !fuelStack.isEmpty() && inputTank.getInvFluid(0).getRawFluid() == Fluids.WATER && !inputTank.getInvFluid(0).isEmpty()) {
				this.burnTime = FuelRegistry.INSTANCE.get(fuelStack.getItem());
				this.fuelTime = this.burnTime;
				if (this.isBurning()) {
					dirty = true;
					if (!fuelStack.isEmpty()) {
						Item item = fuelStack.getItem();
						fuelStack.decrement(1);
						if (fuelStack.isEmpty()) {
							Item remainder = item.getRecipeRemainder();
							this.contents.set(0, remainder == null ? ItemStack.EMPTY : new ItemStack(remainder));
						}
					}
				}
			}

			if (wasBurning != this.isBurning()) {
				dirty = true;
				this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BoilerBlock.LIT, this.isBurning()), 3);
			}
		}

		if (dirty) {
			this.markDirty();
		}
	}

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	protected Text getContainerName() {
		return AssemblyBlocks.BOILER.getName();
	}

	@Override
	public Container createContainer(int syncId, PlayerInventory inventory) {
		return new MenuBuilder(AssemblyConstants.Ids.BOILER).player(inventory.player).inventory().hotbar().addInventory().blockEntity(this).filterSlot(0, 20, 20, AbstractFurnaceBlockEntity::canUseAsFuel).addContainer().create(this, syncId);
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.contains(INPUT_FLUIDS_KEY)) {
			FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(INPUT_FLUIDS_KEY));
			this.inputTank.setInvFluid(0, fluid, Simulation.ACTION);
		}
		if (tag.contains(OUTPUT_FLUIDS_KEY)) {
			FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(OUTPUT_FLUIDS_KEY));
			this.outputCapacity = fluid.getAmount_F();
			this.outputTank.setInvFluid(0, fluid, Simulation.ACTION);
		}
		this.burnTime = tag.getShort(BURN_TIME_KEY);
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		FluidVolume inputFluid = this.inputTank.getInvFluid(0);
		if (!inputFluid.isEmpty()) {
			tag.put(INPUT_FLUIDS_KEY, inputFluid.toTag());
		}
		FluidVolume outputFluid = this.outputTank.getInvFluid(0);
		if (!outputFluid.isEmpty()) {
			tag.put(OUTPUT_FLUIDS_KEY, outputFluid.toTag());
		}
		tag.putShort(BURN_TIME_KEY, (short) this.burnTime);
		return tag;
	}

	public int getBurnTime() {
		return burnTime;
	}

	public int getFuelTime() {
		return fuelTime;
	}

	public IOFluidContainer getInputTank() {
		return inputTank;
	}

	public IOFluidContainer getOutputTank() {
		return outputTank;
	}
}
