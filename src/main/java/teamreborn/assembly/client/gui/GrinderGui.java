package teamreborn.assembly.client.gui;

import teamreborn.assembly.blockentity.GrinderBlockEntity;
import teamreborn.assembly.container.builder.BuiltContainer;

public class GrinderGui extends GuiBase {

	GrinderBlockEntity grinder;

	public GrinderGui(BuiltContainer container) {
		super(container);
		this.grinder = (GrinderBlockEntity) container.getTile();
	}

	@Override
	public void drawSlots() {
		drawSlot(55, 45);
		drawOutputSlot(101, 45);
	}

	@Override
	protected void drawForeground(int i, int i1) {
		super.drawForeground(i, i1);

		drawProgressBar(grinder.getProgressScaled(100), 100, 76, 48,  ProgressDirection.RIGHT);
		drawEnergyBar(9, 19, 50, 50, 100);
	}
}
