package com.terraformersmc.assembly.client.screen;

import com.terraformersmc.assembly.blockentity.TinkeringTableBlockEntity;
import com.terraformersmc.assembly.client.screen.base.BaseSyncedScreen;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TinkeringTableScreen extends BaseSyncedScreen<TinkeringTableBlockEntity> {
	public TinkeringTableScreen(ScreenSyncer<TinkeringTableBlockEntity> syncer) {
		super(syncer);
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int i1) {
		super.drawForeground(matrixStack, i, i1);
		this.drawInfoPanelBackground(matrixStack, 47, 17, Layer.FOREGROUND);
		ItemStack stack = syncer.getBlockEntity().getRenderingStack();
		if (!stack.isEmpty()) {
			Text itemName = stack.getName();
			this.drawTextWithShadow(matrixStack, textRenderer, itemName, 47 + 113 / 2, 23, 0xe0e0e0);
		}
	}
}
