package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Clearable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import com.terraformersmc.assembly.block.SteamPressBlock;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.SteamPressingRecipe;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;

import javax.annotation.Nullable;
import java.util.Iterator;

public class SteamPressBlockEntity extends AssemblySyncedNbtBlockEntity implements Tickable, Clearable, SidedInventory, TankInputInteractable {

	private static final String MASTER_KEY = AssemblyConstants.NbtKeys.MASTER;
	private Boolean master = null;

	private static int MAX_PROGRESS = 5;
	private static int MAX_RESET = 50;
	private static final String PROGRESS_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int progress = 0;
	private static final String RESET_KEY = AssemblyConstants.NbtKeys.PRESS_RESET;
	private int reset = MAX_RESET;

	private static final FluidAmount STEAM_COST_PER_PROGRESS_INCREMENT = FluidAmount.BUCKET.roundedDiv(50);
	private static final FluidAmount STEAM_COST_PER_RESET_INCREMENT = FluidAmount.BUCKET.roundedDiv(200);

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.FLUIDS;
	private static final FluidAmount TANK_CAPACITY = FluidAmount.BUCKET.checkedMul(4);

	private final IOFluidContainer tank;

	public static final int INPUT_SLOT = 0;
	public static final int OUTPUT_SLOT = 1;
	private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
	private static final int[] SIDE_SLOTS = new int[]{INPUT_SLOT, OUTPUT_SLOT};
	private static final int[] BOTTOM_SLOTS = new int[]{OUTPUT_SLOT};
	protected DefaultedList<ItemStack> items;
	private int extractCooldown = 0;

	private static final double LOWEST_ARM_OFFSET = -9 / 16F; // 9/16 because the lowest the arm should go is 9 pixels down.

	private static final String RECIPE_KEY = AssemblyConstants.NbtKeys.RECIPE;
	private SteamPressingRecipe recipe = null;
	private static final String CURRENT_PRESSES_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int currentPresses = 0;

	public SteamPressBlockEntity() {
		super(AssemblyBlockEntities.STEAM_PRESS);
		this.tank = new SimpleIOFluidContainer(1, TANK_CAPACITY, AssemblyConstants.FluidFilters.STEAM);
		this.items = DefaultedList.ofSize(2, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (world != null) {
			if (!world.isClient && isMaster()) {
				if (extractCooldown > 0) {
					extractCooldown--;
				}
				boolean sync = false;
				if (reset < MAX_RESET) {
					if (!tank.attemptExtraction(AssemblyConstants.FluidFilters.STEAM, STEAM_COST_PER_RESET_INCREMENT, Simulation.SIMULATE).isEmpty()) {
						tank.attemptExtraction(AssemblyConstants.FluidFilters.STEAM, STEAM_COST_PER_RESET_INCREMENT, Simulation.ACTION);
						reset++;
						sync = true;
					}
					if (reset == MAX_RESET) {
						progress = 0;
						sync = true;
					}
				} else {
					updateRecipe();
					if (recipe != null) {
						if (!tank.attemptExtraction(AssemblyConstants.FluidFilters.STEAM, STEAM_COST_PER_PROGRESS_INCREMENT, Simulation.SIMULATE).isEmpty()) {
							tank.attemptExtraction(AssemblyConstants.FluidFilters.STEAM, STEAM_COST_PER_PROGRESS_INCREMENT, Simulation.ACTION);
							progress++;
							sync = true;
						}
						if (progress == MAX_PROGRESS) {
							world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.6F, 0.5F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
							if (world instanceof ServerWorld) {
								((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, getRenderStack()), pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0.05);
							}
							currentPresses++;
							sync = true;
							reset = 0;
							if (recipe.presses <= currentPresses) {
								items.set(INPUT_SLOT, ItemStack.EMPTY);
								items.set(OUTPUT_SLOT, recipe.craft(this));
								extractCooldown = 5;
							}
						}
					}
				}
				if (sync) {
					sync();
				}
			}
		}
	}

	private void updateRecipe() {
		if (world != null) {
			recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.STEAM_PRESSING, getMaster(), world).orElse(null);
			if (recipe == null) {
				currentPresses = 0;
				ItemStack input = getStack(INPUT_SLOT);
				if (!input.isEmpty()) {
					setStack(INPUT_SLOT, ItemStack.EMPTY);
					setStack(OUTPUT_SLOT, input);
					extractCooldown = 5;
				}
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag, boolean syncing) {
		if (tag.contains(MASTER_KEY)) {
			this.master = tag.getBoolean(MASTER_KEY);
		}
		this.items = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.fromTag(tag, this.items);
		this.progress = tag.getInt(PROGRESS_KEY);
		this.reset = tag.getInt(RESET_KEY);
		if (!syncing) {
			if (isMaster()) {
				if (tag.contains(FLUIDS_KEY)) {
					FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
					this.tank.setInvFluid(0, fluid, Simulation.ACTION);
				}
			}
			String recipeValue = tag.getString(RECIPE_KEY);
			if (!recipeValue.equals("null")) {
				SteamPressingRecipe recipe = null;
				if (world != null) {
					Recipe<?> gottenRecipe = world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
					if (gottenRecipe instanceof SteamPressingRecipe) {
						recipe = (SteamPressingRecipe) gottenRecipe;
					}
				}
				this.recipe = recipe;
			}
			this.currentPresses = tag.getInt(CURRENT_PRESSES_KEY);
			//Todo: cache recipes
			//updateRecipe();
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag, boolean syncing) {
		if (master != null) {
			tag.putBoolean(MASTER_KEY, master);
		}
		Inventories.toTag(tag, this.items);
		tag.putInt(PROGRESS_KEY, progress);
		tag.putInt(RESET_KEY, reset);
		if (!syncing) {
			if (isMaster()) {
				FluidVolume inputFluid = this.tank.getInvFluid(0);
				if (!inputFluid.isEmpty()) {
					tag.put(FLUIDS_KEY, inputFluid.toTag());
				}
			}
			tag.putString(RECIPE_KEY, recipe == null ? "null" : recipe.getId().toString());
			tag.putInt(CURRENT_PRESSES_KEY, currentPresses);
			//Todo: cache recipes
			//updateRecipe();
		}
		return tag;
	}

	public ItemStack getRenderStack() {
		ItemStack input = getStack(INPUT_SLOT);
		ItemStack output = getStack(OUTPUT_SLOT);
		return isEmpty() ? ItemStack.EMPTY : !input.isEmpty() ? input : output;
	}

	public VoxelShape getArmVoxelShape(DoubleBlockHalf half) {
		return SteamPressBlock.ARM_SHAPE.offset(0, half == DoubleBlockHalf.LOWER ? getArmOffset() + 1 : getArmOffset(), 0);
	}

	public IOFluidContainer getTank() {
		return getMaster().tank;
	}

	private boolean isMaster() {
		if (master == null) {
			if (world != null) {
				master = getCachedState().getBlock() instanceof SteamPressBlock && getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.LOWER;
			} else {
				return false;
			}
		}
		return master;
	}

	private SteamPressBlockEntity getMaster() {
		if (world != null) {
			if (getCachedState().getBlock() instanceof SteamPressBlock && getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
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

	@Override
	public void clear() {
		getMaster().items.clear();
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER ? TOP_SLOTS : new int[0];
		} else if (side == Direction.DOWN) {
			return getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER ? new int[0] : BOTTOM_SLOTS;
		}
		return SIDE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return slot == INPUT_SLOT && this.isEmpty() && stack.getCount() == 1;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == OUTPUT_SLOT && extractCooldown == 0;
	}

	@Override
	public int size() {
		return getMaster().items.size();
	}

	@Override
	public boolean isEmpty() {
		Iterator<ItemStack> iterator = getMaster().items.iterator();
		ItemStack stack;
		do {
			if (!iterator.hasNext()) {
				return true;
			}

			stack = (ItemStack) iterator.next();
		} while (stack.isEmpty());
		return false;
	}

	@Override
	public ItemStack getStack(int slot) {
		return getMaster().items.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot == INPUT_SLOT && amount > 0) {
			updateRecipe();
		}
		if (world != null && !world.isClient) {
			sync();
		}
		return Inventories.splitStack(getMaster().items, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (slot == INPUT_SLOT) {
			updateRecipe();
		}
		if (world != null && !world.isClient) {
			sync();
		}
		return Inventories.removeStack(getMaster().items, slot);
	}

	@Override
	public void setStack(int slot, ItemStack newStack) {
		ItemStack currentStack = getMaster().items.get(slot);
		boolean noUpdate = !newStack.isEmpty() && newStack.isItemEqualIgnoreDamage(currentStack) && ItemStack.areTagsEqual(newStack, currentStack);
		getMaster().items.set(slot, newStack);
		if (newStack.getCount() > this.getMaxCountPerStack()) {
			newStack.setCount(this.getMaxCountPerStack());
		}

		if (slot == INPUT_SLOT && !noUpdate) {
			this.markDirty();
			updateRecipe();
		}
		if (world != null && !world.isClient) {
			sync();
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world != null) {
			if (this.world.getBlockEntity(this.pos) != this) {
				return false;
			} else {
				return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
			}
		}
		return false;
	}

	@Override
	public FluidInsertable getInteractableInsertable() {
		return getTank().getInsertable().getPureInsertable();
	}
}
