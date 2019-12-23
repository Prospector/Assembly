package team.reborn.assembly.blockentity;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import reborncore.common.fluid.container.GenericFluidContainer;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.block.BoilerBlock;
import team.reborn.assembly.container.builder.MenuBuilder;
import team.reborn.assembly.util.FluidTank;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BoilerBlockEntity extends AssemblyContainerBlockEntity implements Tickable, GenericFluidContainer<Direction> {
	private int burnTime;
	private int fuelTime;

	public Map<BlockPos, BlockEntity> chambers = new HashMap<>();

	public static final FluidValue OUTPUT_CAPACITY_PER_CHAMBER = FluidValue.BUCKET.multiply(4);
	public FluidTank outputTank;

	public static final FluidValue INPUT_CAPACITY = FluidValue.BUCKET.multiply(4);
	public FluidTank inputTank;

	public BoilerBlockEntity() {
		super(AssemblyBlockEntities.BOILER);
		inputTank = new FluidTank("", INPUT_CAPACITY, this);
		outputTank = new FluidTank("", FluidValue.EMPTY, this);
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
		return new MenuBuilder(new Identifier(Assembly.MOD_ID, "boiler")).player(inventory.player).inventory().hotbar().addInventory().blockEntity(this).filterSlot(0, 20, 20, AbstractFurnaceBlockEntity::canUseAsFuel).addContainer().create(this, syncId);
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.burnTime = tag.getShort("BurnTime");
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putShort("BurnTime", (short) this.burnTime);
		super.toTag(tag);
		return tag;
	}

	@Override
	public void tick() {
		boolean wasBurning = this.isBurning();
		boolean dirty = false;
		if (this.isBurning()) {
			--this.burnTime;
		}

		chambers.clear();
		for (int i = 1; i < 255; i++) {
			BlockPos chamberPos = pos.offset(Direction.UP, i);
			BlockEntity chamber = world.getBlockEntity(chamberPos);
			if (chamber instanceof BoilerChamberBlockEntity) {
				chambers.put(chamberPos, chamber);
				((BoilerChamberBlockEntity) chamber).updateBoiler(pos);
			} else {
				outputTank.setCapacity(OUTPUT_CAPACITY_PER_CHAMBER.multiply(chambers.size()));
				break;
			}
		}

		if (!this.world.isClient) {
			ItemStack fuelStack = this.contents.get(0);
			if (!this.isBurning() && !fuelStack.isEmpty()) {
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

	public int getBurnTime() {
		return burnTime;
	}

	public int getFuelTime() {
		return fuelTime;
	}

	@Override
	public void setFluid(Direction type, @Nonnull FluidInstance instance) {
		inputTank.setFluid(type, instance);
	}

	@Nonnull
	@Override
	public FluidInstance getFluidInstance(Direction type) {
		return inputTank.getFluidInstance(type);
	}

	@Override
	public FluidValue getCapacity(Direction type) {
		return inputTank.getCapacity();
	}
}
