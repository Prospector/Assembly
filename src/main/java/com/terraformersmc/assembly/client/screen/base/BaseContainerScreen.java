package com.terraformersmc.assembly.client.screen.base;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.terraformersmc.assembly.Assembly;
import com.terraformersmc.assembly.blockentity.base.AssemblyContainerBlockEntity;
import com.terraformersmc.assembly.networking.AssemblyNetworking;
import com.terraformersmc.assembly.screen.builder.*;
import com.terraformersmc.assembly.util.math.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class BaseContainerScreen<BE extends AssemblyContainerBlockEntity> extends HandledScreen<ScreenSyncer<BE>> {

	public static final Identifier GUI_SHEET = new Identifier(Assembly.MOD_ID, "textures/gui/gui_sheet.png");

	private Layer layer = Layer.BACKGROUND;
	protected final ScreenSyncer<BE> syncer;
	protected final BE blockEntity;

	public BaseContainerScreen(ScreenSyncer<BE> syncer) {
		super(syncer, MinecraftClient.getInstance().player.inventory, syncer.getBlockEntity().getDisplayName());
		this.syncer = syncer;
		this.blockEntity = syncer.getBlockEntity();
		this.backgroundWidth = syncer.getWidth();
		this.backgroundHeight = syncer.getHeight();
	}

	@Override
	protected void init() {
		super.init();
		AssemblyNetworking.requestScreenSync(syncer);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float v, int i, int i1) {
		this.layer = Layer.BACKGROUND;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawDefaultBackground(matrixStack, this.x, this.y, this.backgroundWidth, this.backgroundHeight);
		this.drawSlots(matrixStack);
		this.drawTanks(matrixStack);
	}

	public void drawSlots(MatrixStack matrixStack) {
		for (Slot slot : this.syncer.slots) {
			this.drawSlot(matrixStack, slot.x, slot.y);
		}
	}

	public void drawTanks(MatrixStack matrixStack) {
		for (Tank tank : this.syncer.tanks) {
			this.drawTank(matrixStack, tank, Layer.BACKGROUND);
		}
	}

	@Override
	protected void drawForeground(MatrixStack matrixStack, int i, int i1) {
		super.drawForeground(matrixStack, i, i1);
		TextPositioner inventoryTitlePositioner = this.syncer.getInventoryTitlePositioner();
		if (inventoryTitlePositioner != null) {
			Text title = this.playerInventory.getDisplayName();
			ScreenPos pos = inventoryTitlePositioner.position(backgroundWidth, backgroundHeight, title, textRenderer);
			this.textRenderer.draw(matrixStack, title, pos.getX(), pos.getY(), 0x404040);
		}

		TextPositioner titlePositioner = this.syncer.getTitlePositioner();
		if (titlePositioner != null) {
			ScreenPos pos = titlePositioner.position(backgroundWidth, backgroundHeight, title, textRenderer);
			this.textRenderer.draw(matrixStack, title, pos.getX(), pos.getY(), 0x404040);
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.layer = Layer.FOREGROUND;
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawMouseoverTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
		super.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
		for (Tank tank : Lists.reverse(syncer.tanks)) {
			int x = tank.getX() + this.x;
			int y = tank.getY() + this.y;
			TankStyle style = tank.getStyle();
			if (MathUtil.isInRect(mouseX, mouseY, x, y, x + style.width, y + style.height)) {
				this.fillGradient(matrixStack, x + style.fluidXOffset, y + style.fluidYOffset, x + style.fluidXOffset + style.fluidWidth, y + style.fluidYOffset + style.fluidHeight, 0x80FFFFFF, 0x80FFFFFF);
				FluidVolume volume = tank.getVolume();
				if (!volume.isEmpty()) {
					List<Text> tooltip = volume.getTooltipText(this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
					tooltip.add(new TranslatableText("assembly.tank.amount", volume.getAmount_F().toDisplayString()).formatted(Formatting.GRAY));
					this.renderTooltip(matrixStack, tooltip, mouseX, mouseY);
				}
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Tank tank : Lists.reverse(syncer.tanks)) {
			int x = tank.getX() + this.x;
			int y = tank.getY() + this.y;
			TankStyle style = tank.getStyle();
			if (MathUtil.isInRect(mouseX, mouseY, x, y, x + style.width, y + style.height)) {
				syncer.clickTank(tank);
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
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

	public void drawBurnBar(MatrixStack matrixStack, int burnTime, int fuelTime, int x, int y, Layer layer) {
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, x, y, 168, 0, 14, 14);
		if (burnTime > 0) {
			int burnProgress = (int) ((double) burnTime / (double) fuelTime * 13);
			this.drawTexture(matrixStack, x, y + 12 - burnProgress, 182, 12 - burnProgress, 14, burnProgress + 2);
		}
	}

	public void drawTank(MatrixStack matrixStack, Tank tank, Layer layer) {
		int x = tank.getX();
		int y = tank.getY();
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		TankStyle style = tank.getStyle();
		FluidVolume volume = tank.getVolume();
		double capacity = tank.getCapacity().asInexactDouble();
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, x, y, style.x, style.y, style.width, style.height);
		if (!volume.isEmpty()) {
			double percentFull = volume.getAmount_F().asInexactDouble() / capacity;
			double fluidHeight = (double) style.fluidHeight * percentFull;
			volume.renderGuiRect(x + style.fluidXOffset, y + style.fluidYOffset + style.fluidHeight - fluidHeight, x + style.fluidXOffset + style.fluidWidth, y + style.fluidYOffset + style.fluidHeight);
		}
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		this.drawTexture(matrixStack, x + style.fluidXOffset, y + style.fluidYOffset, style.overlayX, style.overlayY, style.fluidWidth, style.fluidHeight);
	}

	public enum Layer {
		BACKGROUND, FOREGROUND
	}

	public enum ProgressDirection {
		RIGHT(84, 151, 100, 151, 16, 10),
		LEFT(100, 161, 84, 161, 16, 10),
		DOWN(104, 171, 114, 171, 10, 16),
		UP(84, 171, 94, 171, 10, 16);

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
