package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtBlockEntity;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.FluidInjectingRecipe;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;
import com.terraformersmc.assembly.util.recipe.RecipeUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Clearable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.io.PrintStream;

public class FluidInjectorBlockEntity extends AssemblySyncedNbtBlockEntity implements Tickable, Clearable, SidedInventory, TankInputInteractable {

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.FLUIDS;
	private static final FluidAmount TANK_CAPACITY = FluidAmount.BUCKET.checkedMul(4);
	private static final String RECIPE_KEY = AssemblyConstants.NbtKeys.RECIPE;

	private final IOFluidContainer tank;

	public static final int SLOT = 0;
	private static final int[] TOP_SLOTS = new int[]{SLOT};
	private static final int[] SIDE_SLOTS = new int[]{SLOT};
	private static final int[] BOTTOM_SLOTS = new int[]{SLOT};
	protected DefaultedList<ItemStack> items;
	private FluidInjectingRecipe recipe = null;

	public FluidInjectorBlockEntity() {
		super(AssemblyBlockEntities.FLUID_INJECTOR);
		this.tank = new SimpleIOFluidContainer(1, TANK_CAPACITY);
		this.items = DefaultedList.ofSize(1, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (this.world != null) {
			if (!this.world.isClient) {
				if (this.recipe != null) {
					System.out.println(this.recipe.getId());
				}
			}
		}
	}

	private void updateRecipe() {
		if (this.world != null) {
			this.recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.FLUID_INJECTING, this, this.world).orElse(null);
		}
	}

	@Override
	public void fromTag(CompoundTag tag, boolean syncing) {
		this.items = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.fromTag(tag, this.items);
		if (!syncing) {
			if (tag.contains(FLUIDS_KEY)) {
				FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
				this.tank.setInvFluid(0, fluid, Simulation.ACTION);
			}
			String recipeValue = tag.getString(RECIPE_KEY);
			if (!recipeValue.equals("null")) {
				FluidInjectingRecipe recipe = null;
				if (this.world != null) {
					Recipe<?> gottenRecipe = this.world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
					if (gottenRecipe instanceof FluidInjectingRecipe) {
						recipe = (FluidInjectingRecipe) gottenRecipe;
					}
				}
				this.recipe = recipe;
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag, boolean syncing) {
		Inventories.toTag(tag, this.items);
		if (!syncing) {
			FluidVolume inputFluid = this.tank.getInvFluid(0);
			if (!inputFluid.isEmpty()) {
				tag.put(FLUIDS_KEY, inputFluid.toTag());
			}
			tag.putString(RECIPE_KEY, this.recipe == null ? "null" : this.recipe.getId().toString());
			//Todo: cache recipes
			//updateRecipe();
		}
		return tag;
	}

	public ItemStack getRenderStack() {
		ItemStack input = this.getStack(SLOT);
		return this.isEmpty() ? ItemStack.EMPTY : input;
	}

	public IOFluidContainer getTank() {
		return this.tank;
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
			return BOTTOM_SLOTS;
		}
		return SIDE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
//		if (RecipeUtil.isValidInput(this.world, AssemblyRecipeTypes.FLUID_INJECTING, stack)) {
			return slot == SLOT && this.isEmpty() && stack.getCount() == 1;
//		}
//		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == SLOT;
	}

	@Override
	public int size() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack item : this.items) {
			if (!item.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.items.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		if (slot == SLOT && amount > 0) {
			this.updateRecipe();
		}
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		return Inventories.splitStack(this.items, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		if (slot == SLOT) {
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

		if (slot == SLOT && !noUpdate) {
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
