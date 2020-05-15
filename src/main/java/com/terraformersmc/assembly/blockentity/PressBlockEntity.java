package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.RawFluidTagFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.block.PressBlock;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtBlockEntity;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.PressingRecipe;
import com.terraformersmc.assembly.sound.AssemblySoundEvents;
import com.terraformersmc.assembly.tag.AssemblyFluidTags;
import com.terraformersmc.assembly.tag.AssemblyItemTags;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;
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
import net.minecraft.util.Clearable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

import javax.annotation.Nullable;
import java.util.Iterator;

public class PressBlockEntity extends AssemblySyncedNbtBlockEntity implements Tickable, Clearable, SidedInventory, TankInputInteractable {

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
	private PressingRecipe recipe = null;
	private static final String CURRENT_PRESSES_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int currentPresses = 0;
	public static final FluidFilter STEAM_FITLTER = new RawFluidTagFilter(AssemblyFluidTags.STEAM);

	public PressBlockEntity() {
		super(AssemblyBlockEntities.PRESS);
		this.tank = new SimpleIOFluidContainer(1, TANK_CAPACITY, STEAM_FITLTER);
		this.items = DefaultedList.ofSize(2, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (this.world != null) {
			if (!this.world.isClient && this.isMaster()) {
				if (this.extractCooldown > 0) {
					this.extractCooldown--;
				}
				boolean sync = false;
				if (this.reset < MAX_RESET) {
					if (!this.tank.attemptExtraction(STEAM_FITLTER, STEAM_COST_PER_RESET_INCREMENT, Simulation.SIMULATE).isEmpty()) {
						this.tank.attemptExtraction(STEAM_FITLTER, STEAM_COST_PER_RESET_INCREMENT, Simulation.ACTION);
						this.reset++;
						sync = true;
					}
					if (this.reset == MAX_RESET) {
						this.progress = 0;
						sync = true;
					}
				} else {
					this.updateRecipe();
					if (this.recipe != null) {
						if (!this.tank.attemptExtraction(STEAM_FITLTER, STEAM_COST_PER_PROGRESS_INCREMENT, Simulation.SIMULATE).isEmpty()) {
							this.tank.attemptExtraction(STEAM_FITLTER, STEAM_COST_PER_PROGRESS_INCREMENT, Simulation.ACTION);
							this.progress++;
							sync = true;
						}
						if (this.progress == MAX_PROGRESS) {
							boolean squishy = getStack(INPUT_SLOT).getItem().isIn(AssemblyItemTags.SQUISHY);
							boolean poppable = getStack(INPUT_SLOT).getItem().isIn(AssemblyItemTags.POPPABLE);
							this.world.playSound(null, this.pos, AssemblySoundEvents.PRESS_HIT, SoundCategory.BLOCKS, squishy || poppable ? 0.2F : 0.6F, 0.5F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.4F);
							if (this.world instanceof ServerWorld) {
								((ServerWorld) this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getRenderStack()), this.pos.getX() + 0.5, this.pos.getY() + 0.7, this.pos.getZ() + 0.5, squishy ? 30 : 10, 0.1, 0.1, 0.1, 0.05);
							}
							if (squishy) {
								this.world.playSound(null, this.pos, AssemblySoundEvents.PRESS_SQUISH, SoundCategory.BLOCKS, 0.6F, 0.5F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.4F);
							}
							if (poppable) {
								this.world.playSound(null, this.pos, AssemblySoundEvents.PRESS_POP, SoundCategory.BLOCKS, 0.6F, 0.5F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.4F);
								if (this.world instanceof ServerWorld) {
									((ServerWorld) this.world).spawnParticles(ParticleTypes.SPLASH, this.pos.getX() + 0.5, this.pos.getY() + 0.7, this.pos.getZ() + 0.5, 30, 0.2, 0.2, 0.2, 0.2);
								}
							}
							this.currentPresses++;
							sync = true;
							this.reset = 0;
							if (this.recipe.presses <= this.currentPresses) {
								this.items.set(INPUT_SLOT, ItemStack.EMPTY);
								this.items.set(OUTPUT_SLOT, this.recipe.craft(this));
								this.extractCooldown = 5;
							}
						}
					}
				}
				if (sync) {
					this.sync();
				}
			}
		}
	}

	private void updateRecipe() {
		if (this.world != null) {
			this.recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.PRESSING, this.getMaster(), this.world).orElse(null);
			if (this.recipe == null) {
				this.currentPresses = 0;
				ItemStack input = this.getStack(INPUT_SLOT);
				if (!input.isEmpty()) {
					this.setStack(INPUT_SLOT, ItemStack.EMPTY);
					this.setStack(OUTPUT_SLOT, input);
					this.extractCooldown = 5;
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
			if (this.isMaster()) {
				if (tag.contains(FLUIDS_KEY)) {
					FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
					this.tank.setInvFluid(0, fluid, Simulation.ACTION);
				}
			}
			String recipeValue = tag.getString(RECIPE_KEY);
			if (!recipeValue.equals("null")) {
				PressingRecipe recipe = null;
				if (this.world != null) {
					Recipe<?> gottenRecipe = this.world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
					if (gottenRecipe instanceof PressingRecipe) {
						recipe = (PressingRecipe) gottenRecipe;
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
		if (this.master != null) {
			tag.putBoolean(MASTER_KEY, this.master);
		}
		Inventories.toTag(tag, this.items);
		tag.putInt(PROGRESS_KEY, this.progress);
		tag.putInt(RESET_KEY, this.reset);
		if (!syncing) {
			if (this.isMaster()) {
				FluidVolume inputFluid = this.tank.getInvFluid(0);
				if (!inputFluid.isEmpty()) {
					tag.put(FLUIDS_KEY, inputFluid.toTag());
				}
			}
			tag.putString(RECIPE_KEY, this.recipe == null ? "null" : this.recipe.getId().toString());
			tag.putInt(CURRENT_PRESSES_KEY, this.currentPresses);
			//Todo: cache recipes
			//updateRecipe();
		}
		return tag;
	}

	public ItemStack getRenderStack() {
		ItemStack input = this.getStack(INPUT_SLOT);
		ItemStack output = this.getStack(OUTPUT_SLOT);
		return this.isEmpty() ? ItemStack.EMPTY : !input.isEmpty() ? input : output;
	}

	public VoxelShape getArmVoxelShape(DoubleBlockHalf half) {
		return PressBlock.ARM_SHAPE.offset(0, half == DoubleBlockHalf.LOWER ? this.getArmOffset() + 1 : this.getArmOffset(), 0);
	}

	public IOFluidContainer getTank() {
		return this.getMaster().tank;
	}

	private boolean isMaster() {
		if (this.master == null) {
			if (this.world != null) {
				this.master = this.getCachedState().getBlock() instanceof PressBlock && this.getCachedState().get(PressBlock.HALF) == DoubleBlockHalf.LOWER;
			} else {
				return false;
			}
		}
		return this.master;
	}

	private PressBlockEntity getMaster() {
		if (this.world != null) {
			if (this.getCachedState().getBlock() instanceof PressBlock && this.getCachedState().get(PressBlock.HALF) == DoubleBlockHalf.UPPER) {
				BlockEntity lowerHalf = this.world.getBlockEntity(this.pos.down());
				if (lowerHalf instanceof PressBlockEntity) {
					return ((PressBlockEntity) lowerHalf);
				}
			}
		}
		return this;
	}

	public double getArmOffset() {
		double lowestPosition = -LOWEST_ARM_OFFSET;
		if (this.getReset() != MAX_RESET) {
			// Reset-based animation (Up)
			return lowestPosition * Math.sin(Math.PI / 2.0 * ((double) this.getReset() / (double) MAX_RESET)) - lowestPosition;
		} else {
			// Progress-based animation (Down)
			return -lowestPosition * Math.sin(Math.PI / 2.0 * ((double) this.getProgress() / (double) MAX_PROGRESS) - Math.PI / 2.0) - lowestPosition;
		}
	}

	private int getProgress() {
		return this.getMaster().progress;
	}

	private int getReset() {
		return this.getMaster().reset;
	}

	@Override
	public void clear() {
		this.getMaster().items.clear();
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return this.getCachedState().get(PressBlock.HALF) == DoubleBlockHalf.UPPER ? TOP_SLOTS : new int[0];
		} else if (side == Direction.DOWN) {
			return this.getCachedState().get(PressBlock.HALF) == DoubleBlockHalf.UPPER ? new int[0] : BOTTOM_SLOTS;
		}
		return SIDE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
//		if (this.world != null && RecipeUtil.isValidInput(this.world, AssemblyRecipeTypes.STEAM_PRESSING, stack)) {
		return slot == INPUT_SLOT && this.isEmpty() && stack.getCount() == 1;
//		}
//		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == OUTPUT_SLOT && this.extractCooldown == 0;
	}

	@Override
	public int size() {
		return this.getMaster().items.size();
	}

	@Override
	public boolean isEmpty() {
		Iterator<ItemStack> iterator = this.getMaster().items.iterator();
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
		return this.getMaster().items.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot == INPUT_SLOT && amount > 0) {
			this.updateRecipe();
		}
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		return Inventories.splitStack(this.getMaster().items, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (slot == INPUT_SLOT) {
			this.updateRecipe();
		}
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		return Inventories.removeStack(this.getMaster().items, slot);
	}

	@Override
	public void setStack(int slot, ItemStack newStack) {
		ItemStack currentStack = this.getMaster().items.get(slot);
		boolean noUpdate = !newStack.isEmpty() && newStack.isItemEqualIgnoreDamage(currentStack) && ItemStack.areTagsEqual(newStack, currentStack);
		this.getMaster().items.set(slot, newStack);
		if (newStack.getCount() > this.getMaxCountPerStack()) {
			newStack.setCount(this.getMaxCountPerStack());
		}

		if (slot == INPUT_SLOT && !noUpdate) {
			this.markDirty();
			this.updateRecipe();
		}
		if (this.world != null && !this.world.isClient) {
			this.sync();
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
		return this.getTank().getInsertable().getPureInsertable();
	}
}
