package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.block.BoilerBlock;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.BoilingRecipe;
import com.terraformersmc.assembly.recipe.provider.FluidInputInventory;
import com.terraformersmc.assembly.screenhandler.builder.ScreenHandlerBuilder;
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
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
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

	private static final FluidAmount RECIPE_OUTPUT_INCREMENT = FluidAmount.BUCKET.roundedDiv(1000);
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
	}

	@Override
	public void tick() {
		boolean wasBurning = this.isBurning();
		boolean dirty = false;

		this.chambers.clear();
		for (int i = 1; i < 255; i++) {
			BlockPos chamberPos = this.pos.offset(Direction.UP, i);
			BlockEntity chamber = this.world.getBlockEntity(chamberPos);
			if (chamber instanceof BoilerChamberBlockEntity) {
				this.chambers.put(chamberPos, chamber);
				((BoilerChamberBlockEntity) chamber).updateBoiler(this.pos);
			} else {
				this.outputCapacity = OUTPUT_CAPACITY_PER_CHAMBER.checkedMul(this.chambers.size());
				break;
			}
		}

		if (this.world != null && !this.world.isClient) {
			this.updateRecipe();
			if (this.recipe != null) {
				ItemStack fuelStack = this.contents.get(0);
				if (this.isBurning() || !fuelStack.isEmpty()) {
					Pair<FluidVolume, FluidVolume> boilResult = this.boil();
					FluidVolume input = boilResult.getLeft();
					FluidVolume output = boilResult.getRight();
					if (output != null) {
						if (this.isBurning()) {
							--this.burnTime;
							this.inputTank.attemptExtraction(fluidKey -> input.getFluidKey().equals(fluidKey), input.getAmount_F(), Simulation.ACTION);
							this.outputTank.attemptInsertion(output, Simulation.ACTION);
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
					if (wasBurning != this.isBurning()) {
						dirty = true;
						this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BoilerBlock.LIT, this.isBurning()), 3);
					}
				}
			}
		}

		if (dirty) {
			this.markDirty();
		}
	}

	private void updateRecipe() {
		if (this.world != null) {
			this.recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.BOILING, this, this.world).orElse(null);
		}
	}

	/**
	 * @return Boiling input and output or null if boiling isn't possible
	 */
	@Nullable
	private Pair<FluidVolume, FluidVolume> boil() {
		FluidVolume output = this.outputTank.getInvFluid(0);
		if (!this.inputTank.getInvFluid(0).isEmpty() && (output.isEmpty() || (output.getFluidKey() == this.recipe.getOutputVolume().getFluidKey() && output.getAmount_F().isLessThan(this.outputTank.getCapacity(0))))) {
			FluidVolume fromInput = this.inputTank.attemptExtraction(this.recipe.getInputIngredient().getFilter(), this.recipe.getInputIngredient().getAmount(), Simulation.SIMULATE);
			if (!fromInput.isEmpty() && recipe.getInputIngredient().test(fromInput)) {
				FluidVolume excessOutput = this.outputTank.attemptInsertion(recipe.getOutputVolume(), Simulation.SIMULATE);
				if (excessOutput.isEmpty()) {
					return new Pair<>(fromInput, recipe.getOutputVolume());
				}
			}
		}
		return null;
	}

	@Override
	protected Text getContainerName() {
		return AssemblyBlocks.BOILER.getName();
	}

	@Override
	public ScreenHandler createContainer(int syncId, PlayerInventory inventory) {
		return new ScreenHandlerBuilder(AssemblyConstants.Ids.BOILER).player(inventory.player).inventory().hotbar().addInventory().blockEntity(this).filterSlot(0, 20, 20, AbstractFurnaceBlockEntity::canUseAsFuel).addContainer().create(this, syncId);
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
		String recipeValue = tag.getString(RECIPE_KEY);
		if (!recipeValue.equals("null")) {
			BoilingRecipe recipe = null;
			if (this.world != null) {
				Recipe<?> gottenRecipe = this.world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
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
		//Todo: cache recipes
		//updateRecipe();
		tag.putString(RECIPE_KEY, this.recipe == null ? "null" : this.recipe.getId().toString());
		return tag;
	}

	public int getBurnTime() {
		return this.burnTime;
	}

	public int getFuelTime() {
		return this.fuelTime;
	}

	public IOFluidContainer getInputTank() {
		return this.inputTank;
	}

	public IOFluidContainer getOutputTank() {
		return this.outputTank;
	}

	@Override
	public FluidVolume getFluidInput() {
		return this.inputTank.getInvFluid(0);
	}

	@Override
	public FluidInsertable getInteractableInsertable() {
		return this.getInputTank().getInsertable().getPureInsertable();
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		return this.getInputTank().getExtractable().getPureExtractable();
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
}