package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.terraformersmc.assembly.blockentity.base.AssemblyContainerBlockEntity;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtContainerBlockEntity;
import com.terraformersmc.assembly.recipe.AssemblyRecipeTypes;
import com.terraformersmc.assembly.recipe.InjectingRecipe;
import com.terraformersmc.assembly.screen.builder.ScreenHandlerBuilder;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.screen.builder.TankStyle;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.fluid.IOFluidContainer;
import com.terraformersmc.assembly.util.fluid.SimpleIOFluidContainer;
import com.terraformersmc.assembly.util.interaction.interactable.TankInputInteractable;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public class InjectorBlockEntity extends AssemblySyncedNbtContainerBlockEntity implements Tickable, SidedInventory, TankInputInteractable {

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.FLUIDS;
	private static final FluidAmount TANK_CAPACITY = FluidAmount.BUCKET.checkedMul(4);
	private static final String RECIPE_KEY = AssemblyConstants.NbtKeys.RECIPE;

	private final IOFluidContainer tank;

	public static final int INPUT_SLOT = 0;
	public static final int OUTPUT_SLOT = 1;
	private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
	private static final int[] SIDE_SLOTS = new int[]{INPUT_SLOT};
	private static final int[] BOTTOM_SLOTS = new int[]{OUTPUT_SLOT};
	private InjectingRecipe recipe = null;
	private int progress = 0;

	public InjectorBlockEntity() {
		super(AssemblyBlockEntities.INJECTOR);
		this.tank = new SimpleIOFluidContainer(1, TANK_CAPACITY);
	}

	@Override
	public void tick() {
		if (this.world != null) {
			if (!this.world.isClient) {
				if (this.recipe != null) {
					System.out.println(recipe.getId());
				} else if (FluidAttributes.INSERTABLE.get(getStack(INPUT_SLOT)) != RejectingFluidInsertable.NULL) {

				}
			}
		}
	}

	private void updateRecipe() {
		if (this.world != null) {
			this.recipe = this.world.getRecipeManager().getFirstMatch(AssemblyRecipeTypes.INJECTING, this, this.world).orElse(null);
		}
	}

	@Override
	public void fromTag(CompoundTag tag, boolean syncing) {
		if (!syncing) {
			if (tag.contains(FLUIDS_KEY)) {
				FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
				this.tank.setInvFluid(0, fluid, Simulation.ACTION);
			}
			String recipeValue = tag.getString(RECIPE_KEY);
			if (!recipeValue.equals("null")) {
				InjectingRecipe recipe = null;
				if (this.world != null) {
					Recipe<?> gottenRecipe = this.world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
					if (gottenRecipe instanceof InjectingRecipe) {
						recipe = (InjectingRecipe) gottenRecipe;
					}
				}
				this.recipe = recipe;
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag, boolean syncing) {
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
		ItemStack input = this.getStack(INPUT_SLOT);
		return this.isEmpty() ? ItemStack.EMPTY : input;
	}

	public IOFluidContainer getTank() {
		return this.tank;
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
		return slot == INPUT_SLOT && this.isEmpty() && stack.getCount() == 1;
//		}
//		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot == INPUT_SLOT;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack stack = super.removeStack(slot, amount);
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		if (slot == INPUT_SLOT && amount > 0) {
			this.updateRecipe();
		}
		return stack;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack stack = super.removeStack(slot);
		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		if (slot == INPUT_SLOT) {
			this.updateRecipe();
		}
		return stack;
	}

	@Override
	public void setStack(int slot, ItemStack newStack) {
		ItemStack currentStack = this.contents.get(slot);
		boolean noUpdate = !newStack.isEmpty() && newStack.isItemEqualIgnoreDamage(currentStack) && ItemStack.areTagsEqual(newStack, currentStack);
		this.contents.set(slot, newStack);
		if (newStack.getCount() > this.getMaxCountPerStack()) {
			newStack.setCount(this.getMaxCountPerStack());
		}

		if (this.world != null && !this.world.isClient) {
			this.sync();
		}
		if (slot == INPUT_SLOT && !noUpdate) {
			this.markDirty();
			this.updateRecipe();
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

	@Override
	public ScreenSyncer<AssemblyContainerBlockEntity> createSyncer(int syncId, PlayerInventory inventory) {
		return new ScreenHandlerBuilder(AssemblyConstants.Ids.INJECTOR, 176, 166, this)
				.player(inventory.player)
				.inventory()
				.hotbar()
				.addInventory()

				.container(this)
				.sync(this::getProgress, this::setProgress)
				.slot(INPUT_SLOT, 20, 40)
				.outputSlot(OUTPUT_SLOT, 40, 40)
				.tank(60, 23, TankStyle.TWO, tank)
				.addContainer()

				.create(this, syncId);
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.assembly.injecting");
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	protected boolean syncContents() {
		return true;
	}
}
