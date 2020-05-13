package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.RawFluidTagFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtBlockEntity;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.PressingRecipe;
import com.terraformersmc.assembly.tag.AssemblyFluidTags;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;
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

import javax.annotation.Nullable;
import java.util.Iterator;

public class SqueezerBlockEntity extends AssemblySyncedNbtBlockEntity implements Tickable, Clearable, SidedInventory, TankInputInteractable {

	private static int MAX_PROGRESS = 5;
	private static int MAX_RESET = 50;
	private static final String PROGRESS_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int progress = 0;
	private static final String RESET_KEY = AssemblyConstants.NbtKeys.PRESS_RESET;
	private int reset = MAX_RESET;

	private static final FluidAmount STEAM_COST_PER_PROGRESS_INCREMENT = FluidAmount.BUCKET.roundedDiv(80);
	private static final FluidAmount STEAM_COST_PER_RESET_INCREMENT = FluidAmount.BUCKET.roundedDiv(120);

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.FLUIDS;
	private static final FluidAmount TANK_CAPACITY = FluidAmount.BUCKET.checkedMul(4);

	private final IOFluidContainer tank;

	public static final int INPUT_SLOT = 0;
	private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
	private static final int[] SIDE_SLOTS = new int[]{INPUT_SLOT};
	protected DefaultedList<ItemStack> items;
	private int extractCooldown = 0;

	private static final String RECIPE_KEY = AssemblyConstants.NbtKeys.RECIPE;
	private PressingRecipe recipe = null;
	private static final String CURRENT_PRESSES_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int currentPresses = 0;
	public static final FluidFilter STEAM_FITLTER = new RawFluidTagFilter(AssemblyFluidTags.STEAM);

	public SqueezerBlockEntity() {
		super(AssemblyBlockEntities.PRESS);
		this.tank = new SimpleIOFluidContainer(1, TANK_CAPACITY, STEAM_FITLTER);
		this.items = DefaultedList.ofSize(2, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (this.world != null) {
			if (!this.world.isClient) {
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
							this.world.playSound(null, this.pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.6F, 0.5F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.4F);
							if (this.world instanceof ServerWorld) {
								((ServerWorld) this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getRenderStack()), this.pos.getX() + 0.5, this.pos.getY() + 0.7, this.pos.getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0.05);
							}
							this.currentPresses++;
							sync = true;
							this.reset = 0;
							if (this.recipe.presses <= this.currentPresses) {
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
			this.recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.PRESSING, this, this.world).orElse(null);
			if (this.recipe == null) {
				this.currentPresses = 0;
				ItemStack input = this.getStack(INPUT_SLOT);
				if (!input.isEmpty()) {
					this.extractCooldown = 5;
				}
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag, boolean syncing) {
		this.items = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.fromTag(tag, this.items);
		this.progress = tag.getInt(PROGRESS_KEY);
		this.reset = tag.getInt(RESET_KEY);
		if (!syncing) {
			if (tag.contains(FLUIDS_KEY)) {
				FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
				this.tank.setInvFluid(0, fluid, Simulation.ACTION);
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
		Inventories.toTag(tag, this.items);
		tag.putInt(PROGRESS_KEY, this.progress);
		tag.putInt(RESET_KEY, this.reset);
		if (!syncing) {
			FluidVolume inputFluid = this.tank.getInvFluid(0);
			if (!inputFluid.isEmpty()) {
				tag.put(FLUIDS_KEY, inputFluid.toTag());
			}
			tag.putString(RECIPE_KEY, this.recipe == null ? "null" : this.recipe.getId().toString());
			tag.putInt(CURRENT_PRESSES_KEY, this.currentPresses);
			//Todo: cache recipes
			//updateRecipe();
		}
		return tag;
	}

	public ItemStack getRenderStack() {
		return this.getStack(INPUT_SLOT);
	}

	public IOFluidContainer getTank() {
		return this.tank;
	}

	private int getProgress() {
		return this.progress;
	}

	private int getReset() {
		return this.reset;
	}

	@Override
	public void clear() {
		this.items.clear();
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return TOP_SLOTS;
		} else if (side == Direction.DOWN) {
			return new int[0];
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
		return this.extractCooldown == 0;
	}

	@Override
	public int size() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		Iterator<ItemStack> iterator = this.items.iterator();
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
		return this.items.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot == INPUT_SLOT && amount > 0) {
			this.updateRecipe();
		}
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		return Inventories.splitStack(this.items, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (slot == INPUT_SLOT) {
			this.updateRecipe();
		}
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		return Inventories.removeStack(this.items, slot);
	}

	@Override
	public void setStack(int slot, ItemStack newStack) {
		ItemStack currentStack = this.items.get(slot);
		boolean noUpdate = !newStack.isEmpty() && newStack.isItemEqualIgnoreDamage(currentStack) && ItemStack.areTagsEqual(newStack, currentStack);
		this.items.set(slot, newStack);
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
