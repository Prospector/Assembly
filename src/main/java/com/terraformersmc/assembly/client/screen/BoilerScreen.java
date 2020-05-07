package com.terraformersmc.assembly.client.screen;

import com.terraformersmc.assembly.blockentity.BoilerBlockEntity;
import com.terraformersmc.assembly.client.screen.base.BaseSyncedScreen;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import net.minecraft.client.util.math.MatrixStack;

public class BoilerScreen extends BaseSyncedScreen<BoilerBlockEntity> {
	public BoilerScreen(ScreenSyncer<BoilerBlockEntity> syncer) {
		super(syncer);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int i1) {
		super.drawForeground(matrixStack, i, i1);
		this.drawBurnBar(matrixStack, this.syncer.getBlockEntity().getBurnTime(), this.syncer.getBlockEntity().getFuelTime(), 80, 23, Layer.FOREGROUND);
	}
}
