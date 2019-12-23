package team.reborn.assembly.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import team.reborn.assembly.Assembly;
import team.reborn.assembly.container.builder.BuiltMenu;

public class BaseScreen extends AbstractContainerScreen {

	public static final Identifier GUI_SHEET = new Identifier(Assembly.MOD_ID, "textures/gui/gui_sheet.png");
	public int xSize = 176;
	public int ySize = 176;

	private Layer layer = Layer.BACKGROUND;
	final BuiltMenu containerProvider;

	public BaseScreen(BuiltMenu menu, String title) {
		super(menu, MinecraftClient.getInstance().player.inventory, new LiteralText(title));
		this.containerProvider = menu;
	}

	@Override
	protected void drawBackground(float v, int i, int i1) {
		layer = Layer.BACKGROUND;
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawDefaultBackground(this.x, this.y, xSize, ySize);
		drawPlayerSlots(this.x + xSize / 2, this.y + 93, true);
		drawSlots();
	}

	//Best time to draw slots
	public void drawSlots() {

	}

	@Override
	protected void drawForeground(int i, int i1) {
		super.drawForeground(i, i1);
		font.draw(containerProvider.getName().toString(), 10, 6, 4210752);
		font.draw("Inventory", 10, 80, 4210752);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.renderBackground();
		layer = Layer.FOREGROUND;
		super.render(mouseX, mouseY, partialTicks);
		this.drawMouseoverTooltip(mouseX, mouseY);
	}

	public void drawDefaultBackground(int x, int y, int width, int height) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		blit(x, y, 0, 0, width / 2, height / 2);
		blit(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
		blit(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
		blit(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
	}

	public void drawPlayerSlots(int posX, int posY, boolean center) {
		if (center) {
			posX -= 81;
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				blit(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
			}
		}

		for (int x = 0; x < 9; x++) {
			blit(posX + x * 18, posY + 58, 150, 0, 18, 18);
		}
	}

	public void drawSlot(int posX, int posY) {
		if (layer == Layer.BACKGROUND) {
			posX += this.x;
			posY += this.y;
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		blit(posX - 1, posY - 1, 150, 0, 18, 18);
	}

	public void drawOutputSlot(int x, int y) {
		if (layer == Layer.BACKGROUND) {
			x += this.x;
			y += this.y;
		}
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		blit(x - 5, y - 5, 150, 18, 26, 26);
	}

	public void drawEnergyBar(int x, int y, int height, int energyStored, int maxEnergyStored) {
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);

		blit(x, y, 0, 150, 14, height);
		blit(x, y + height - 1, 0, 255, 14, 1);
		int draw = (int) ((double) energyStored / (double) maxEnergyStored * (height - 2));
		blit(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);

		//TODO hover tooltip
	}

	public void drawProgressBar(int progress, int maxProgress, int x, int y, ProgressDirection direction) {
		//		if (layer == GuiBase.Layer.BACKGROUND) {
		//			x += left;
		//			y += top;
		//		}

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().getTextureManager().bindTexture(GUI_SHEET);
		blit(x, y, direction.x, direction.y, direction.width, direction.height);
		int j = (int) ((double) progress / (double) maxProgress * 16);
		if (j < 0) {
			j = 0;
		}

		switch (direction) {
			case RIGHT:
				blit(x, y, direction.xActive, direction.yActive, j, 10);
				break;
			case LEFT:
				blit(x + 16 - j, y, direction.xActive + 16 - j, direction.yActive, j, 10);
				break;
			case UP:
				blit(x, y + 16 - j, direction.xActive, direction.yActive + 16 - j, 10, j);
				break;
			case DOWN:
				blit(x, y, direction.xActive, direction.yActive, 10, j);
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
