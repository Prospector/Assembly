package com.terraformersmc.assembly.blockentity.base;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;

public abstract class AssemblySyncedNbtContainerBlockEntity extends AssemblyContainerBlockEntity implements BlockEntityClientSerializable {

	public AssemblySyncedNbtContainerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public final void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.fromTag(tag, false);
	}

	@Override
	public final CompoundTag toTag(CompoundTag tag) {
		return this.toTag(super.toTag(tag), false);
	}

	@Override
	public final void fromClientTag(CompoundTag tag) {
		this.fromTag(tag, true);
		if (syncContents()) {
			Inventories.fromTag(tag, this.contents);
		}
	}

	@Override
	public final CompoundTag toClientTag(CompoundTag tag) {
		if (syncContents()) {
			return this.toTag(Inventories.toTag(tag, this.contents, true), true);
		}
		return this.toTag(tag, true);
	}

	public abstract void fromTag(CompoundTag tag, boolean syncing);

	public abstract CompoundTag toTag(CompoundTag tag, boolean syncing);

	protected boolean syncContents() {
		return false;
	}
}
