package team.reborn.assembly.blockentity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import team.reborn.assembly.attributes.IOFluidContainer;
import team.reborn.assembly.attributes.SimpleIOFluidContainer;
import team.reborn.assembly.block.SteamPressBlock;
import team.reborn.assembly.fluid.AssemblyFluids;
import team.reborn.assembly.recipe.AssemblyRecipeTypes;
import team.reborn.assembly.recipe.PressingRecipe;
import team.reborn.assembly.recipe.provider.PressingRecipeProvider;
import team.reborn.assembly.util.AssemblyConstants;

import javax.annotation.Nullable;
import java.util.Iterator;

public class SteamPressBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable, Clearable, SidedInventory, PressingRecipeProvider {

	private static int MAX_PROGRESS = 5;
	private static int MAX_RESET = 30;
	private static final String PROGRESS_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int progress = 0;
	private static final String RESET_KEY = AssemblyConstants.NbtKeys.PRESS_RESET;
	private int reset = MAX_RESET;

	private static final FluidKey STEAM = FluidKeys.get(AssemblyFluids.STEAM);
	private static final FluidAmount STEAM_COST_PER_PROGRESS = FluidAmount.BUCKET.roundedDiv(100);
	private static final FluidAmount STEAM_COST_PER_RESET = FluidAmount.BUCKET.roundedDiv(100);

	private static final String FLUIDS_KEY = AssemblyConstants.NbtKeys.FLUIDS;
	private static final FluidAmount TANK_CAPACITY = FluidAmount.BUCKET.checkedMul(4);
	private final IOFluidContainer tank;

	private static final int INPUT_SLOT = 0;
	private static final int OUTPUT_SLOT = 1;
	private static final int[] TOP_SLOTS = new int[]{INPUT_SLOT};
	private static final int[] SIDE_SLOTS = new int[]{INPUT_SLOT, OUTPUT_SLOT};
	private static final int[] BOTTOM_SLOTS = new int[]{OUTPUT_SLOT};
	protected DefaultedList<ItemStack> items;

	private static final double LOWEST_ARM_OFFSET = -9 / 16F; // 9/16 because the lowest the arm should go is 9 pixels down.

	private static final String RECIPE_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private PressingRecipe recipe = null;
	private static final String CURRENT_PRESSES_KEY = AssemblyConstants.NbtKeys.PRESS_PROGRESS;
	private int currentPresses = 0;

	public SteamPressBlockEntity() {
		super(AssemblyBlockEntities.STEAM_PRESS);
		this.tank = new SimpleIOFluidContainer(1, TANK_CAPACITY);
		this.items = DefaultedList.ofSize(2, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		MAX_PROGRESS = 5;
		MAX_RESET = 50;
		if (world != null) {
			if (!world.isClient && isMaster()) {
				if (reset < MAX_RESET) {
					if (!tank.attemptExtraction(STEAM::equals, STEAM_COST_PER_RESET, Simulation.SIMULATE).isEmpty()) {
						tank.attemptExtraction(STEAM::equals, STEAM_COST_PER_RESET, Simulation.ACTION);
						reset++;
					}
					if (reset == MAX_RESET) {
						progress = 0;
					}
					sync();
				} else if (recipe != null) {
					if (!tank.attemptExtraction(STEAM::equals, STEAM_COST_PER_PROGRESS, Simulation.SIMULATE).isEmpty()) {
						tank.attemptExtraction(STEAM::equals, STEAM_COST_PER_PROGRESS, Simulation.ACTION);
						progress++;
					}
					if (progress == MAX_PROGRESS) {
						world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.6F, 0.5F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
						if (world instanceof ServerWorld) {
							((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, getRenderStack()), pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0.05);
						}
						currentPresses++;
						reset = 0;
						if (recipe.presses <= currentPresses) {
							items.set(INPUT_SLOT, ItemStack.EMPTY);
							items.set(OUTPUT_SLOT, recipe.craft(this));
							updateRecipe();
						}
					}
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
			}
		}
	}

	public ItemStack getRenderStack() {
		ItemStack input = getInvStack(INPUT_SLOT);
		ItemStack output = getInvStack(OUTPUT_SLOT);
		return isInvEmpty() ? ItemStack.EMPTY : !input.isEmpty() ? input : output;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.items = DefaultedList.ofSize(this.getInvSize(), ItemStack.EMPTY);
		Inventories.fromTag(tag, this.items);
		if (tag.contains(FLUIDS_KEY)) {
			FluidVolume fluid = FluidVolume.fromTag(tag.getCompound(FLUIDS_KEY));
			this.tank.setInvFluid(0, fluid, Simulation.ACTION);
		}
		this.progress = tag.getInt(PROGRESS_KEY);
		this.reset = tag.getInt(RESET_KEY);
		String recipeValue = tag.getString(RECIPE_KEY);
		if (!recipeValue.equals("null")) {
			PressingRecipe recipe = null;
			if (world != null) {
				Recipe<?> gottenRecipe = world.getRecipeManager().get(new Identifier(recipeValue)).orElse(null);
				if (gottenRecipe instanceof PressingRecipe) {
					recipe = (PressingRecipe) gottenRecipe;
				}
			}
			this.recipe = recipe;
		}
		this.currentPresses = tag.getInt(CURRENT_PRESSES_KEY);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag = super.toTag(tag);
		Inventories.toTag(tag, this.items);
		FluidVolume inputFluid = this.tank.getInvFluid(0);
		if (!inputFluid.isEmpty()) {
			tag.put(FLUIDS_KEY, inputFluid.toTag());
		}
		tag.putInt(PROGRESS_KEY, progress);
		tag.putInt(RESET_KEY, reset);
		tag.putString(RECIPE_KEY, recipe == null ? "null" : recipe.getId().toString());
		tag.putInt(CURRENT_PRESSES_KEY, currentPresses);
		return tag;
	}

	public VoxelShape getArmVoxelShape(DoubleBlockHalf half) {
		return SteamPressBlock.ARM_SHAPE.offset(0, half == DoubleBlockHalf.LOWER ? getArmOffset() + 1 : getArmOffset(), 0);
	}

	public IOFluidContainer getTank() {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			BlockEntity lowerHalf = world.getBlockEntity(pos.down());
			if (state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER && lowerHalf instanceof SteamPressBlockEntity) {
				return ((SteamPressBlockEntity) lowerHalf).tank;
			}
		}
		return tank;
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	private boolean isMaster() {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			return state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.LOWER;
		}
		return false;
	}

	private SteamPressBlockEntity getMaster() {
		if (world != null) {
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof SteamPressBlock && state.get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER) {
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
	public int[] getInvAvailableSlots(Direction side) {
		if (side == Direction.UP) {
			return getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER ? TOP_SLOTS : new int[0];
		} else if (side == Direction.DOWN) {
			return getCachedState().get(SteamPressBlock.HALF) == DoubleBlockHalf.UPPER ? new int[0] : BOTTOM_SLOTS;
		}
		return SIDE_SLOTS;
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir) {
		return slot == INPUT_SLOT && isInvEmpty();
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
		return slot == OUTPUT_SLOT;
	}

	@Override
	public int getInvSize() {
		return getMaster().items.size();
	}

	@Override
	public boolean isInvEmpty() {
		Iterator iterator = getMaster().items.iterator();
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
	public ItemStack getInvStack(int slot) {
		return getMaster().items.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (slot == INPUT_SLOT && amount > 0) {
			updateRecipe();
		}
		sync();
		return Inventories.splitStack(getMaster().items, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (slot == INPUT_SLOT) {
			updateRecipe();
		}
		sync();
		return Inventories.removeStack(getMaster().items, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack newStack) {
		ItemStack currentStack = getMaster().items.get(slot);
		boolean noUpdate = !newStack.isEmpty() && newStack.isItemEqualIgnoreDamage(currentStack) && ItemStack.areTagsEqual(newStack, currentStack);
		getMaster().items.set(slot, newStack);
		if (newStack.getCount() > this.getInvMaxStackAmount()) {
			newStack.setCount(this.getInvMaxStackAmount());
		}

		if (slot == INPUT_SLOT && !noUpdate) {
			this.markDirty();
			updateRecipe();
		}
		sync();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
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
	public int getCurrentPresses() {
		return currentPresses;
	}
}
