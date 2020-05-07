package com.terraformersmc.assembly.screen.builder;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Nameable;

public interface ScreenSyncerProvider<BE extends BlockEntity & Nameable> {
	ScreenSyncer<BE> createSyncer(int syncId, PlayerInventory inventory);
}
