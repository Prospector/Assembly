package teamreborn.assembly.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import teamreborn.assembly.blockentity.GrinderBlockEntity;

public class GrinderGui extends ContainerGui {
	private static final Identifier BG_TEX = new Identifier("textures/gui/container/furnace.png");

	public GrinderGui(PlayerEntity playerEntity, GrinderBlockEntity blockEntity) {
		super(blockEntity.createContainer(playerEntity.inventory, playerEntity));
	}

	@Override
	protected void drawBackground(float v, int i, int i1) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.client.getTextureManager().bindTexture(BG_TEX);int var4 = this.left;
		int var5 = this.top;
		this.drawTexturedRect(var4, var5, 0, 0, this.containerWidth, this.containerHeight);

	}
}
