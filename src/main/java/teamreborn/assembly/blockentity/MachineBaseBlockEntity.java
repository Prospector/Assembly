package teamreborn.assembly.blockentity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import teamreborn.assembly.container.FabricContainerProvider;
import teamreborn.assembly.recipe.IRecipeCrafterProvider;
import teamreborn.assembly.recipe.RecipeCrafter;

public abstract class MachineBaseBlockEntity extends InventoryBaseBlockEntity implements Tickable, IRecipeCrafterProvider, FabricContainerProvider {

	RecipeCrafter recipeCrafter;

	public MachineBaseBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
		recipeCrafter = new RecipeCrafter(getContainerIdentifier(), this, 1, 1, this, new int[]{0}, new int[]{1});
	}

	@Override
	public RecipeCrafter getRecipeCrafter() {
		return recipeCrafter;
	}

	@Override
	public void tick() {
		recipeCrafter.updateEntity();
	}

	public int getProgressScaled(final int scale) {
		if (recipeCrafter != null && recipeCrafter.currentTickTime != 0) {
			return recipeCrafter.currentTickTime * scale / recipeCrafter.currentNeededTicks;
		}
		return 0;
	}


	@Override
	public void fromTag(CompoundTag compoundTag) {
		recipeCrafter.readFromNBT(compoundTag);
		super.fromTag(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		recipeCrafter.writeToNBT(compoundTag);
		return super.toTag(compoundTag);
	}
}
