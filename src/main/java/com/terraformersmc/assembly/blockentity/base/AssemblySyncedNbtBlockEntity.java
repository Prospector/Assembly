package com.terraformersmc.assembly.blockentity.base;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class AssemblySyncedNbtBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

	public AssemblySyncedNbtBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
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
	}

	@Override
	public final CompoundTag toClientTag(CompoundTag tag) {
		return this.toTag(tag, true);
	}

	public abstract void fromTag(CompoundTag tag, boolean syncing);

	public abstract CompoundTag toTag(CompoundTag tag, boolean syncing);
}
