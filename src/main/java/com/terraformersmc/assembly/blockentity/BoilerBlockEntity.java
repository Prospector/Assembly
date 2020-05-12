package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.block.BoilerBlock;
import com.terraformersmc.assembly.blockentity.base.AssemblyContainerBlockEntity;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.BoilingRecipe;
import com.terraformersmc.assembly.recipe.provider.FluidInputInventory;
import com.terraformersmc.assembly.screen.builder.ScreenHandlerBuilder;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.screen.builder.TankStyle;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankIOInteractable;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BoilerBlockEntity extends AssemblyContainerBlockEntity implements Tickable, FluidInputInventory, TankIOInteractable, SidedInventory {

	private static final int FUEL_SLOT = 0;
	private static final int[] BOTTOM_SLOTS = new int[]{FUEL_SLOT};
	private static final int[] SIDE_SLOTS = new int[]{FUEL_SLOT};

	private static final String BURN_TIME_KEY = AssemblyConstants.NbtKeys.BURN_TIME;
	private static final String FUEL_TIME_KEY = AssemblyConstants.NbtKeys.FUEL_TIME;
	private int burnTime;
	private int fuelTime;

	private Map<BlockPos, BlockEntity> chambers = new HashMap<>();

	private static final String INPUT_FLUIDS_KEY = AssemblyConstants.NbtKeys.INPUT_FLUIDS;
	private static final FluidAmount INPUT_CAPACITY = FluidAmount.BUCKET.checkedMul(4);
	private IOFluidContainer inputTank;

	private static final String OUTPUT_FLUIDS_KEY = AssemblyConstants.NbtKeys.OUTPUT_FLUIDS;
	private static final FluidAmount OUTPUT_CAPACITY_PER_CHAMBER = FluidAmount.BUCKET.checkedMul(4);
	private IOFluidContainer outputTank;
	private FluidAmount outputCapacity = FluidAmount.ZERO;

	private FluidAmount recipeIncrement = FluidAmount.BUCKET.roundedDiv(1000);
	private static final String RECIPE_KEY = AssemblyConstants.NbtKeys.RECIPE;
	private BoilingRecipe recipe = null;

	public BoilerBlockEntity() {
		super(AssemblyBlockEntities.BOILER);
		this.inputTank = new SimpleIOFluidContainer(1, INPUT_CAPACITY);
		this.outputTank = new SimpleIOFluidContainer(1, () -> this.outputCapacity);
		this.inputTank.addListener((inv, tank, previous, current) -> {
			if (previous.getFluidKey() != current.getFluidKey() || previous.isEmpty() && !current.isEmpty()) {
				//Todo: cache recipes
				//updateRecipe();
			}
		}, () -> {
		});
		this.outputTank.addListener((inv, tank1, previous, current) -> {
			for (BlockEntity chamber : chambers.values()) {
				chamber.markDirty();
			}
		}, () -> {
		});
	}

	@Override
	public void tick() {
		boolean wasBurning = this.isBurning();
		boolean dirty = false;

		chambers.clear();
		for (int i = 1; i < 255; i++) {
			BlockPos chamberPos = pos.offset(Direction.UP, i);
			BlockEntity chamber = world.getBlockEntity(chamberPos);
			if (chamber instanceof BoilerChamberBlockEntity) {
				chambers.put(chamberPos, chamber);
				((BoilerChamberBlockEntity) chamber).updateBoiler(pos);
			} else {
				outputCapacity = OUTPUT_CAPACITY_PER_CHAMBER.checkedMul(chambers.size());
				if (outputTank.getInvFluid(0).getAmount_F().isGreaterThan(outputCapacity)) {
					outputTank.setInvFluid(0, outputTank.getInvFluid(0).withAmount(outputCapacity), Simulation.ACTION);
				}
				double numerator = (Math.sqrt((double) (chambers.size() - 1) / 2.0D) + 1.0D); // original equation: y = [sqrt(x/2) + 1]/1000
				int denominator = 1000;
				recipeIncrement = FluidAmount.of((int) (numerator * 100), denominator * 100); // the 100s are to keep some more digits
				break;
			}
		}

		if (this.world != null && !this.world.isClient) {
			updateRecipe();
			if (isBurning()) {
				--this.burnTime;
			}
			if (this.recipe != null) {
				ItemStack fuelStack = this.contents.get(0);
				if (isBurning() || !fuelStack.isEmpty()) {
					FluidVolume inputSim = simulateCraft();
					if (inputSim != null) {
						if (isBurning()) {
							inputTank.attemptExtraction(inputSim.fluidKey::equals, inputSim.getAmount_F(), Simulation.ACTION);
							outputTank.attemptInsertion(FluidKeys.get(recipe.output).withAmount(recipeIncrement), Simulation.ACTION);
						} else {
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
					}
				}
			}
		}
		if (wasBurning != this.isBurning()) {
			dirty = true;
			this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BoilerBlock.LIT, this.isBurning()), 3);
		}

		if (dirty) {
			this.markDirty();
		}
	}

	private void updateRecipe() {
		if (world != null) {
			recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.BOILING, this, world).orElse(null);
		}
	}

	@Nullable
	private FluidVolume simulateCraft() {
		FluidVolume output = outputTank.getInvFluid(0);
		if (!inputTank.getInvFluid(0).isEmpty() && (output.isEmpty() || (output.getRawFluid() == recipe.output && output.getAmount_F().isLessThan(outputTank.getCapacity(0))))) {
			FluidVolume inputSim = inputTank.attemptExtraction(fluidKey -> FluidKeys.get(recipe.inputFluid).equals(fluidKey), recipeIncrement.roundedMul(recipe.ratio), Simulation.SIMULATE);
			if (!inputSim.isEmpty()) {
				FluidVolume outputSim = outputTank.attemptInsertion(FluidKeys.get(recipe.output).withAmount(recipeIncrement), Simulation.SIMULATE);
				if (!outputSim.getAmount_F().equals(recipeIncrement)) {
					return inputSim;
				}
			}
		}
		return null;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.assembly.boiling");
	}

	@Override
	public ScreenSyncer<AssemblyContainerBlockEntity> createSyncer(int syncId, PlayerInventory inventory) {
		return new ScreenHandlerBuilder(AssemblyConstants.Ids.BOILER, 176, 166, this)
				.player(inventory.player)
				.inventory()
				.hotbar()
				.addInventory()

				.container(this)
				.sync(this::getBurnTime, burnTime -> this.burnTime = burnTime)
				.sync(this::getFuelTime, fuelTime -> this.fuelTime = fuelTime)
				.slot(FUEL_SLOT, 80, 40, AbstractFurnaceBlockEntity::canUseAsFuel)
				.tank(41, 23, TankStyle.TWO, inputTank)
				.outputTank(113, 23, TankStyle.TWO, outputTank)
				.addContainer()

				.create(this, syncId);
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
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
		this.fuelTime = tag.getShort(FUEL_TIME_KEY);
		String recipeValue = tag.getString(RECIPE_KEY);
		if (!recipeValue.equals("null")) {
			BoilingRecipe recipe = null;
			if (world != null) {
				Recipe<?> gottenRecipe = world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
				if (gottenRecipe instanceof BoilingRecipe) {
					recipe = (BoilingRecipe) gottenRecipe;
				}
			}
			this.recipe = recipe;
		}
		//Todo: cache recipes
		//updateRecipe();
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		FluidVolume inputFluid = this.inputTank.getInvFluid(0);
		if (!inputFluid.isEmpty()) {
			tag.put(INPUT_FLUIDS_KEY, inputFluid.toTag());
		}
		FluidVolume outputFluid = this.outputTank.getInvFluid(0);
		if (!outputFluid.isEmpty()) {
			tag.put(OUTPUT_FLUIDS_KEY, outputFluid.toTag());
		}
		tag.putShort(BURN_TIME_KEY, (short) this.burnTime);
		tag.putShort(FUEL_TIME_KEY, (short) this.fuelTime);
		//Todo: cache recipes
		//updateRecipe();
		tag.putString(RECIPE_KEY, recipe == null ? "null" : recipe.getId().toString());
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

	public FluidAmount getRecipeIncrement() {
		return recipeIncrement;
	}

	@Override
	public FluidVolume getFluidInput() {
		return inputTank.getInvFluid(0);
	}

	@Override
	public FluidInsertable getInteractableInsertable() {
		return getInputTank().getInsertable().getPureInsertable();
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		return getInputTank().getExtractable().getPureExtractable();
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.DOWN) {
			return BOTTOM_SLOTS;
		} else {
			return SIDE_SLOTS;
		}
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN && slot == 1) {
			Item item = stack.getItem();
			return item == Items.WATER_BUCKET || item == Items.BUCKET;
		}

		return true;
	}

	public int getChamberCount() {
		return chambers.size();
	}
}
