package teamreborn.assembly.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import teamreborn.assembly.blockentity.GrinderBlockEntity;

public class GrinderGui extends GuiBase {

	public GrinderGui(PlayerEntity playerEntity, GrinderBlockEntity blockEntity) {
		super(blockEntity, playerEntity);
	}

	@Override
	public void drawSlots() {
		drawSlot(55, 45);
		drawOutputSlot(101, 45);
	}

	@Override
	protected void drawForeground(int i, int i1) {
		super.drawForeground(i, i1);

		drawProgressBar(50, 100, 76, 48,  ProgressDirection.RIGHT);
		drawEnergyBar(9, 19, 50, 50, 100);
	}
}
