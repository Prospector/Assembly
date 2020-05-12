package com.terraformersmc.assembly.blockentity;

import com.terraformersmc.assembly.blockentity.base.AssemblyContainerBlockEntity;
import com.terraformersmc.assembly.blockentity.base.AssemblySyncedNbtContainerBlockEntity;
import com.terraformersmc.assembly.item.exoframe.ExoframeModule;
import com.terraformersmc.assembly.item.exoframe.ExoframePieceItem;
import com.terraformersmc.assembly.screen.builder.ScreenHandlerBuilder;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.util.AssemblyConstants;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TinkeringTableBlockEntity extends AssemblySyncedNbtContainerBlockEntity {

	private static final int ITEM_SLOT = 0;
	private static final int UPGRADE_SLOT = 1;

	public TinkeringTableBlockEntity() {
		super(AssemblyBlockEntities.TINKERING_TABLE);
	}

	public ItemStack getRenderingStack() {
		return contents.get(ITEM_SLOT);
	}

	@Override
	public ScreenSyncer<AssemblyContainerBlockEntity> createSyncer(int syncId, PlayerInventory inventory) {
		return new ScreenHandlerBuilder(AssemblyConstants.Ids.TINKERING_TABLE, 176, 179, this)
				.player(inventory.player)
				.inventory()
				.hotbar()
				.addInventory()

				.container(this)
				.slot(ITEM_SLOT, 17, 32, 1, stack -> stack.getItem() instanceof ExoframePieceItem)
				.slot(UPGRADE_SLOT, 17, 52, stack -> stack.getItem() instanceof ExoframeModule)
				.addContainer()

				.create(this, syncId);
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.assembly.tinkering");
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	protected boolean syncContents() {
		return true;
	}

	@Override
	public void fromTag(CompoundTag tag, boolean syncing) {

	}

	@Override
	public CompoundTag toTag(CompoundTag tag, boolean syncing) {
		return tag;
	}
}
