package teamreborn.assembly.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.container.builder.BuiltContainer;

public class GuiBase extends ContainerGui {

	public static final Identifier GUI_SHEET = new Identifier(Assembly.MOD_ID, "textures/gui/gui_sheet.png");
	public int xSize = 176;
	public int ySize = 176;

	private Layer layer = Layer.BACKGROUND;
	final BuiltContainer containerProvider;

	public GuiBase(BuiltContainer container) {
		super(container);
		this.containerProvider = container;
	}

	@Override
	protected void drawBackground(float v, int i, int i1) {
		layer = Layer.BACKGROUND;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawDefaultBackground(left, top, xSize, ySize);
		drawPlayerSlots(left + xSize / 2, top + 93, true);
		drawSlots();
	}

	//Best time to draw slots
	public void drawSlots() {

	}

	@Override
	protected void drawForeground(int i, int i1) {
		super.drawForeground(i, i1);
		fontRenderer.draw(containerProvider.getName().toString(), 10, 6, 4210752);
		fontRenderer.draw("Inventory", 10, 80, 4210752);
	}

	@Override
	public void draw(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground();
		layer = Layer.FOREGROUND;
		super.draw(mouseX, mouseY, partialTicks);
		this.drawMousoverTooltip(mouseX, mouseY);
	}

	public void drawDefaultBackground(int x, int y, int width, int height) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		drawTexturedRect(x, y, 0, 0, width / 2, height / 2);
		drawTexturedRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
		drawTexturedRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
		drawTexturedRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
	}

	public void drawPlayerSlots(int posX, int posY, boolean center) {
		if (center) {
			posX -= 81;
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				drawTexturedRect(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
			}
		}

		for (int x = 0; x < 9; x++) {
			drawTexturedRect(posX + x * 18, posY + 58, 150, 0, 18, 18);
		}
	}

	public void drawSlot(int posX, int posY) {
		if (layer == Layer.BACKGROUND) {
			posX += left;
			posY += top;
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		drawTexturedRect(posX - 1, posY - 1, 150, 0, 18, 18);
	}

	public void drawOutputSlot(int x, int y) {
		if (layer == Layer.BACKGROUND) {
			x += left;
			y += top;
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		drawTexturedRect(x - 5, y - 5, 150, 18, 26, 26);
	}

	public void drawEnergyBar(int x, int y, int height, int energyStored, int maxEnergyStored) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);

		drawTexturedRect(x, y, 0, 150, 14, height);
		drawTexturedRect(x, y + height - 1, 0, 255, 14, 1);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
		drawTexturedRect(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

		//TODO hover tooltip
	}

	public void drawProgressBar(int progress, int maxProgress, int x, int y, ProgressDirection direction) {
		//		if (layer == GuiBase.Layer.BACKGROUND) {
		//			x += left;
		//			y += top;
		//		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		drawTexturedRect(x, y, direction.x, direction.y, direction.width, direction.height);
		int j = (int) ((double) progress / (double) maxProgress * 16);
		if (j < 0) {
			j = 0;
		}

		switch (direction) {
			case RIGHT:
				drawTexturedRect(x, y, direction.xActive, direction.yActive, j, 10);
				break;
			case LEFT:
				drawTexturedRect(x + 16 - j, y, direction.xActive + 16 - j, direction.yActive, j, 10);
				break;
			case UP:
				drawTexturedRect(x, y + 16 - j, direction.xActive, direction.yActive + 16 - j, 10, j);
				break;
			case DOWN:
				drawTexturedRect(x, y, direction.xActive, direction.yActive, 10, j);
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
