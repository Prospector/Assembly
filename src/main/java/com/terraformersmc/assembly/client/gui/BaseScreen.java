package com.terraformersmc.assembly.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.screenhandler.builder.BuiltScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class BaseScreen extends HandledScreen<BuiltScreenHandler> {

	public static final Identifier GUI_SHEET = new Identifier(Assembly.MOD_ID, "textures/gui/gui_sheet.png");
	public int xSize = 176;
	public int ySize = 176;

	private Layer layer = Layer.BACKGROUND;
	final BuiltScreenHandler menu;

	public BaseScreen(BuiltScreenHandler menu, String title) {
		super(menu, MinecraftClient.getInstance().player.inventory, new LiteralText(title));
		this.menu = menu;
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float v, int i, int i1) {
		this.layer = Layer.BACKGROUND;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawDefaultBackground(matrixStack, this.x, this.y, this.xSize, this.ySize);
		this.drawPlayerSlots(matrixStack, this.x + this.xSize / 2, this.y + 93, true);
		this.drawSlots(matrixStack);
	}

	//Best time to draw slots
	public void drawSlots(MatrixStack matrixStack) {
		for (Slot slot : this.menu.slots) {
			this.drawSlot(matrixStack, slot.x, slot.y);
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int i1) {
		super.drawForeground(matrixStack, i, i1);
		this.textRenderer.draw(matrixStack, this.menu.getName().toString(), 10, 6, 4210752);
		this.textRenderer.draw(matrixStack, "Inventory", 10, 80, 4210752);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.layer = Layer.FOREGROUND;
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	public void drawDefaultBackground(MatrixStack matrixStack, int x, int y, int width, int height) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, x, y, 0, 0, width / 2, height / 2);
		this.drawTexture(matrixStack, x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
		this.drawTexture(matrixStack, x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
		this.drawTexture(matrixStack, x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
	}

	public void drawPlayerSlots(MatrixStack matrixStack, int posX, int posY, boolean center) {
		if (center) {
			posX -= 81;
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.drawTexture(matrixStack, posX + x * 18, posY + y * 18, 150, 0, 18, 18);
			}
		}

		for (int x = 0; x < 9; x++) {
			this.drawTexture(matrixStack, posX + x * 18, posY + 58, 150, 0, 18, 18);
		}
	}

	public void drawSlot(MatrixStack matrixStack, int posX, int posY) {
		if (this.layer == Layer.BACKGROUND) {
			posX += this.x;
			posY += this.y;
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, posX - 1, posY - 1, 150, 0, 18, 18);
	}

	public void drawOutputSlot(MatrixStack matrixStack, int x, int y) {
		if (this.layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, x - 5, y - 5, 150, 18, 26, 26);
	}

	public void drawEnergyBar(MatrixStack matrixStack, int x, int y, int height, int energyStored, int maxEnergyStored) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);

		this.drawTexture(matrixStack, x, y, 0, 150, 14, height);
		this.drawTexture(matrixStack, x, y + height - 1, 0, 255, 14, 1);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
		this.drawTexture(matrixStack, x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

		//TODO hover tooltip
	}

	public void drawProgressBar(MatrixStack matrixStack, int progress, int maxProgress, int x, int y, ProgressDirection direction) {
		//		if (layer == GuiBase.Layer.BACKGROUND) {
		//			x += left;
		//			y += top;
		//		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, x, y, direction.x, direction.y, direction.width, direction.height);
		int j = (int) ((double) progress / (double) maxProgress * 16);
		if (j < 0) {
			j = 0;
		}

		switch (direction) {
			case RIGHT:
				this.drawTexture(matrixStack, x, y, direction.xActive, direction.yActive, j, 10);
				break;
			case LEFT:
				this.drawTexture(matrixStack, x + 16 - j, y, direction.xActive + 16 - j, direction.yActive, j, 10);
				break;
			case UP:
				this.drawTexture(matrixStack, x, y + 16 - j, direction.xActive, direction.yActive + 16 - j, 10, j);
				break;
			case DOWN:
				this.drawTexture(matrixStack, x, y, direction.xActive, direction.yActive, 10, j);
				break;
			default:
				return;
		}
		//TODO hover tooltip
	}

	public enum Layer {
		BACKGROUND, FOREGROUND
	}

	public enum ProgressDirection {
		RIGHT(84, 151, 100, 151, 16, 10), LEFT(100, 161, 84, 161, 16, 10), DOWN(104, 171, 114, 171, 10, 16), UP(84, 171, 94, 171, 10, 16);
		public int x;
		public int y;
		public int xActive;
		public int yActive;
		public int width;
		public int height;

		ProgressDirection(int x, int y, int xActive, int yActive, int width, int height) {
			this.x = x;
			this.y = y;
			this.xActive = xActive;
			this.yActive = yActive;
			this.width = width;
			this.height = height;
		}
	}
}
