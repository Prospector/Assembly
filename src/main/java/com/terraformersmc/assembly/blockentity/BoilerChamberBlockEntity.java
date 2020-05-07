package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import com.terraformersmc.assembly.block.AssemblyBlocks;
import com.terraformersmc.assembly.screen.builder.ScreenHandlerBuilder;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import com.terraformersmc.assembly.screen.builder.ScreenSyncerProvider;
import com.terraformersmc.assembly.screen.builder.TankStyle;
import com.terraformersmc.assembly.util.AssemblyConstants;
import com.terraformersmc.assembly.util.interaction.interactable.TankOutputInteractable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

public class BoilerChamberBlockEntity extends BlockEntity implements TankOutputInteractable, ScreenSyncerProvider<BoilerChamberBlockEntity>, Nameable {

	private BoilerBlockEntity boiler;

	public BoilerChamberBlockEntity() {
		super(AssemblyBlockEntities.BOILER_CHAMBER);
	}

	public void updateBoiler(BlockPos pos) {
		if (this.world == null || pos == null) {
			return;
		}
		BlockEntity blockEntity = this.world.getBlockEntity(pos);
		if (blockEntity instanceof BoilerBlockEntity) {
			this.boiler = (BoilerBlockEntity) blockEntity;
		} else {
			this.boiler = null;
		}
	}

	public BoilerBlockEntity getBoiler() {
		return this.boiler;
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		if (this.getBoiler() != null) {
			return this.getBoiler().getOutputTank().getExtractable().getPureExtractable();
		}
		return EmptyFluidExtractable.NULL;
	}

	@Override
	public Text getName() {
		return AssemblyBlocks.BOILER_CHAMBER.getName();
	}

	@Override
	public ScreenSyncer<BoilerChamberBlockEntity> createSyncer(int syncId, PlayerInventory inventory) {
		return new ScreenHandlerBuilder(AssemblyConstants.Ids.BOILER_CHAMBER, 176, 154, this)
				.player(inventory.player)
				.inventory()
				.hotbar()
				.addInventory()

				.container(this)
				.tank(77, 17, boiler.getChamberCount() == 1 ? TankStyle.ONE : TankStyle.TWO, boiler.getOutputTank(), 0)
				.addContainer()

				.create(this, syncId);
	}
}
