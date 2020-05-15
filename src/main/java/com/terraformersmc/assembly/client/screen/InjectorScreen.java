package com.terraformersmc.assembly.client.screen;

import com.terraformersmc.assembly.blockentity.InjectorBlockEntity;
import com.terraformersmc.assembly.blockentity.SqueezerBlockEntity;
import com.terraformersmc.assembly.client.screen.base.BaseSyncedScreen;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import net.minecraft.client.util.math.MatrixStack;

public class InjectorScreen extends BaseSyncedScreen<InjectorBlockEntity> {
	public InjectorScreen(ScreenSyncer<InjectorBlockEntity> syncer) {
		super(syncer);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int i1) {
		super.drawForeground(matrixStack, i, i1);
	}
}
