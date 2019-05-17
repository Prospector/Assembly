///*
// * Copyright (c) 2018 modmuss50 and Gigabit101
// *
// *
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// *
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// *
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//
//package teamreborn.assembly.recipe;
//
//import io.github.prospector.silk.util.ItemUtils;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.recipe.Ingredient;
//import net.minecraft.util.Identifier;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Use this in your tile entity to craft things
// */
//public class RecipeCrafter {
//
//	private static final Logger LOGGER = LogManager.getLogger();
//
//	/**
//	 * This is the recipe type to use
//	 */
//	public Identifier recipeType;
//
//	/**
//	 * This is the parent tile
//	 */
//	public BlockEntity parentTile;
//
//	/**
//	 * This is the amount of inputs that the setRecipe has
//	 */
//	public int inputs;
//
//	/**
//	 * This is the amount of outputs that the recipe has
//	 */
//	public int outputs;
//
//	/**
//	 * This is the inventory to use for the crafting
//	 */
//	public Inventory inventory;
//
//	/**
//	 * This is the list of the slots that the crafting logic should look for the
//	 * input item stacks.
//	 */
//	public int[] inputSlots;
//
//	/**
//	 * This is the list for the slots that the crafting logic should look fot
//	 * the output item stacks.
//	 */
//	public int[] outputSlots;
//	public MachineRecipe currentRecipe;
//	public int currentTickTime = 0;
//	public int currentNeededTicks = 1;// Set to 1 to stop rare crashes
//	double lastEnergy;
//
//	int ticksSinceLastChange;
//
//	public RecipeCrafter(Identifier recipeType, BlockEntity parentTile, int inputs, int outputs, Inventory inventory,
//	                     int[] inputSlots, int[] outputSlots) {
//		this.recipeType = recipeType;
//		this.parentTile = parentTile;
//		this.inputs = inputs;
//		this.outputs = outputs;
//		this.inventory = inventory;
//		this.inputSlots = inputSlots;
//		this.outputSlots = outputSlots;
//		if (!(parentTile instanceof IRecipeCrafterProvider)) {
//			LOGGER.error(parentTile.getClass().getName() + " does not use IRecipeCrafterProvider report this to the issue tracker!");
//		}
//	}
//
//	/**
//	 * Call this on the tile tick
//	 */
//	public void updateEntity() {
//		if (parentTile.getWorld().isClient) {
//			return;
//		}
//		ticksSinceLastChange++;
//		// Force a has chanced every second
//		if (ticksSinceLastChange == 20) {
//			setInvDirty(true);
//			ticksSinceLastChange = 0;
//		}
//		// It will now look for new recipes.
//		if (currentRecipe == null && isInvDirty()) {
//			updateCurrentRecipe();
//		}
//		if (currentRecipe != null) {
//			// If it doesn't have all the inputs reset
//			if (isInvDirty() && !hasAllInputs()) {
//				currentRecipe = null;
//				currentTickTime = 0;
//				setIsActive();
//			}
//			// If it has reached the recipe tick time
//			if (currentRecipe != null && currentTickTime >= currentNeededTicks && hasAllInputs()) {
//				boolean canGiveInvAll = true;
//				// Checks to see if it can fit the output
//				for (int i = 0; i < currentRecipe.getOutputs().size(); i++) {
//					if (!canFitStack(currentRecipe.getOutputs().get(i), outputSlots[i], currentRecipe.checkTags())) {
//						canGiveInvAll = false;
//					}
//				}
//				// The slots that have been filled
//				ArrayList<Integer> filledSlots = new ArrayList<>();
//				if (canGiveInvAll && currentRecipe.onCraft(parentTile)) {
//					for (int i = 0; i < currentRecipe.getOutputs().size(); i++) {
//						// Checks it has not been filled
//						if (!filledSlots.contains(outputSlots[i])) {
//							// Fills the slot with the output stack
//							fitStack(currentRecipe.getOutputs().get(i).copy(), outputSlots[i]);
//							filledSlots.add(outputSlots[i]);
//						}
//					}
//					// This uses all the inputs
//					useAllInputs();
//					// Reset
//					currentRecipe = null;
//					currentTickTime = 0;
//					updateCurrentRecipe();
//					//Update active sate if the tile isnt going to start crafting again
//					if (currentRecipe == null) {
//						setIsActive();
//					}
//				}
//			} else if (currentRecipe != null && currentTickTime < currentNeededTicks) {
//				// This uses the power
//				//				if (energy.canUseEnergy(getEuPerTick(currentRecipe.power()))) {
//				//					energy.useEnergy(getEuPerTick(currentRecipe.power()));
//				//					// Increase the ticktime
//				//					currentTickTime ++;
//				//				}
//
//				currentTickTime++;
//			}
//		}
//		setInvDirty(false);
//	}
//
//	public void updateCurrentRecipe() {
//		currentTickTime = 0;
//		for (MachineRecipe recipe : getRecipes(recipeType)) {
//			// This checks to see if it has all of the inputs
//			if (recipe.canCraft(parentTile) && hasAllInputs(recipe)) {
//				// This checks to see if it can fit all of the outputs
//				for (int i = 0; i < recipe.getOutputs().size(); i++) {
//					if (!canFitStack(recipe.getOutputs().get(i), outputSlots[i], recipe.checkTags())) {
//						currentRecipe = null;
//						this.currentTickTime = 0;
//						setIsActive();
//						return;
//					}
//				}
//				// Sets the current recipe then syncs
//				setCurrentRecipe(recipe);
//				this.currentNeededTicks = Math.max(currentRecipe.tickTime(), 1);
//				this.currentTickTime = 0;
//				setIsActive();
//				return;
//			}
//		}
//	}
//
//	public boolean hasAllInputs() {
//		if (currentRecipe == null) {
//			return false;
//		}
//		return hasAllInputs(currentRecipe);
//	}
//
//	public boolean hasAllInputs(MachineRecipe recipeType) {
//		if (recipeType == null) {
//			return false;
//		}
//		for (Ingredient ingredient : recipeType.getInputs()) {
//			boolean hasItem = false;
//			for (int inputslot : inputSlots) {
//				ItemStack stack = inventory.getInvStack(inputslot);
//				if (ingredient.method_8093(stack)) {
//					hasItem = true;
//				}
//			}
//			if (!hasItem)
//				return false;
//		}
//		return true;
//	}
//
//	public void useAllInputs() {
//		if (currentRecipe == null) {
//			return;
//		}
//		for (Ingredient ingredient : currentRecipe.getInputs()) {
//			for (int inputSlot : inputSlots) {// Uses all of the inputs
//				ItemStack stack = inventory.getInvStack(inputSlot);
//				if (ingredient.method_8093(stack)) {
//					int count = 1;
//					//TODO find a way to support sizes of more than 1
//					if (inventory.getInvStack(inputSlot).getAmount() >= count) {
//						inventory.getInvStack(inputSlot).subtractAmount(count);
//						break;
//					}
//				}
//			}
//		}
//	}
//
//	public boolean canFitStack(ItemStack stack, int slot, boolean oreDic) {// Checks to see if it can fit the stack
//		if (stack.isEmpty()) {
//			return true;
//		}
//		if (inventory.getInvStack(slot).isEmpty()) {
//			return true;
//		}
//		if (ItemUtils.isItemEqual(inventory.getInvStack(slot), stack, true, true, oreDic)) {
//			if (stack.getAmount() + inventory.getInvStack(slot).getAmount() <= stack.getMaxAmount()) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public void fitStack(ItemStack stack, int slot) {// This fits a stack into a slot
//		if (stack.isEmpty()) {
//			return;
//		}
//		if (inventory.getInvStack(slot).isEmpty()) {// If the slot is empty set the contents
//			inventory.setInvStack(slot, stack);
//			return;
//		}
//		if (ItemUtils.isItemEqual(inventory.getInvStack(slot), stack, true, true, currentRecipe.checkTags())) {// If the slot has stuff in
//			if (stack.getAmount() + inventory.getInvStack(slot).getAmount() <= stack.getMaxAmount()) {// Check to see if it fits
//				ItemStack newStack = stack.copy();
//				newStack.setAmount(inventory.getInvStack(slot).getAmount() + stack.getAmount());// Sets
//				// the
//				// new
//				// stack
//				// size
//				inventory.setInvStack(slot, newStack);
//			}
//		}
//	}
//
//	public void readFromNBT(CompoundTag tag) {
//		CompoundTag data = tag.getCompound("Crater");
//
//		if (data.containsKey("currentTickTime"))
//			currentTickTime = data.getInt("currentTickTime");
//
//		if (parentTile != null && parentTile.getWorld() != null && parentTile.getWorld().isClient) {
//			//			parentTile.getWorld().notifyBlockUpdate(parentTile.getPos(),
//			//				parentTile.getWorld().getBlockState(parentTile.getPos()),
//			//				parentTile.getWorld().getBlockState(parentTile.getPos()), 3);
//			//			parentTile.getWorld().markBlockRangeForRenderUpdate(parentTile.getPos().getX(), parentTile.getPos().getY(),
//			//				parentTile.getPos().getZ(), parentTile.getPos().getX(), parentTile.getPos().getY(),
//			//				parentTile.getPos().getZ());
//		}
//	}
//
//	public void writeToNBT(CompoundTag tag) {
//
//		CompoundTag data = new CompoundTag();
//
//		data.putDouble("currentTickTime", currentTickTime);
//
//		tag.put("Crater", data);
//	}
//
//	private boolean isActive() {
//		return currentRecipe != null;// && energy.getEnergy() >= currentRecipe.power();
//	}
//
//	public boolean canCraftAgain() {
//		for (MachineRecipe recipe : getRecipes(recipeType)) {
//			if (recipe.canCraft(parentTile) && hasAllInputs(recipe)) {
//				boolean canGiveInvAll = true;
//				for (int i = 0; i < recipe.getOutputs().size(); i++) {
//					if (!canFitStack(recipe.getOutputs().get(i), outputSlots[i], recipe.checkTags())) {
//						canGiveInvAll = false;
//						return false;
//					}
//				}
//				//				if (energy.getEnergy() < recipe.power()) {
//				//					return false;
//				//				}
//				return canGiveInvAll;
//			}
//		}
//		return false;
//	}
//
//	public void setIsActive() {
//		//		if (parentTile.getWorld().getBlockState(parentTile.getPos()).getBlock() instanceof BlockMachineBase) {
//		//			BlockMachineBase blockMachineBase = (BlockMachineBase) parentTile.getWorld()
//		//				.getBlockState(parentTile.getPos()).getBlock();
//		//			boolean isActive = isActive() || canCraftAgain();
//		//			blockMachineBase.setActive(isActive, parentTile.getWorld(), parentTile.getPos());
//		//		}
//		//		parentTile.getWorld().notifyBlockUpdate(parentTile.getPos(),
//		//			parentTile.getWorld().getBlockState(parentTile.getPos()),
//		//			parentTile.getWorld().getBlockState(parentTile.getPos()), 3);
//	}
//
//	public void setCurrentRecipe(MachineRecipe recipe) {
//		this.currentRecipe = recipe;
//	}
//
//	public boolean isInvDirty() {
//		//		if (inventory instanceof Inventory) {
//		//			return ((Inventory) inventory).isDirty;
//		//		} else if (inventory instanceof reborncore.common.util.Inventory) {
//		//			return ((reborncore.common.util.Inventory) inventory).hasChanged;
//		//		}
//		return true;
//	}
//
//	public void setInvDirty(boolean isDiry) {
//		//		if (inventory instanceof Inventory) {
//		//			((Inventory) inventory).isDirty = isDiry;
//		//		} else if (inventory instanceof reborncore.common.util.Inventory) {
//		//			((reborncore.common.util.Inventory) inventory).hasChanged = isDiry;
//		//		}
//	}
//
////	private List<MachineRecipe> getRecipes(Identifier recipeName) {
////		return getRecipes().stream().filter(machineRecipe -> machineRecipe.getType().equals(recipeName)).collect(Collectors.toList());
////	}
//
////	private List<MachineRecipe> getRecipes() {
////		return new ArrayList<>(MachineRecipeManager.INSTANCE.recipes.values());
////	}
//}
