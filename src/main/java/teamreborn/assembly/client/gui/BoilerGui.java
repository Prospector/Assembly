package teamreborn.assembly.client.gui;

import teamreborn.assembly.blockentity.BoilerBlockEntity;
import teamreborn.assembly.container.builder.BuiltContainer;

public class BoilerGui extends GuiBase {

	BoilerBlockEntity blockEntity;

	public BoilerGui(BuiltContainer container) {
		super(container);
		this.blockEntity = (BoilerBlockEntity) container.getBlockEntity();
	}

	@Override
	public void drawSlots() {
		drawSlot(55, 45);
		drawOutputSlot(101, 45);
	}

	@Override
	protected void drawForeground(int i, int i1) {
		super.drawForeground(i, i1);

		drawProgressBar(blockEntity.getProgressScaled(100), 100, 76, 48,  ProgressDirection.RIGHT);
		drawEnergyBar(9, 19, 50, 50, 100);
	}
}
