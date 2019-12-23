package team.reborn.assembly.blockentity;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.block.AssemblyBlocks;
import team.reborn.assembly.block.BoilerBlock;
import team.reborn.assembly.container.builder.MenuBuilder;

public class BoilerBlockEntity extends AssemblyContainerBlockEntity implements Tickable {
	private int burnTime;
	private int fuelTime;

	public BoilerBlockEntity() {
		super(AssemblyBlockEntities.BOILER);
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
}
