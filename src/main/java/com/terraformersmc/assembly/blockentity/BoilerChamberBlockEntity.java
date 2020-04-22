package com.terraformersmc.assembly.blockentity;

import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import com.terraformersmc.assembly.util.interaction.interactable.TankOutputInteractable;

public class BoilerChamberBlockEntity extends BlockEntity implements TankOutputInteractable {

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
}
