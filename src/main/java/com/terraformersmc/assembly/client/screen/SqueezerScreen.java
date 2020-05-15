package com.terraformersmc.assembly.client.screen;

import com.terraformersmc.assembly.blockentity.SqueezerBlockEntity;
import com.terraformersmc.assembly.blockentity.TinkeringTableBlockEntity;
import com.terraformersmc.assembly.client.screen.base.BaseSyncedScreen;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import net.minecraft.client.util.math.MatrixStack;

public class SqueezerScreen extends BaseSyncedScreen<SqueezerBlockEntity> {
	public SqueezerScreen(ScreenSyncer<SqueezerBlockEntity> syncer) {
		super(syncer);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int i1) {
		super.drawForeground(matrixStack, i, i1);
	}
}
