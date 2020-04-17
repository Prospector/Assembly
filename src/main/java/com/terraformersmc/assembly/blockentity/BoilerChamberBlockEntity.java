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
		if (world == null || pos == null) {
			return;
		}
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BoilerBlockEntity) {
			boiler = (BoilerBlockEntity) blockEntity;
		} else {
			boiler = null;
		}
	}

	public BoilerBlockEntity getBoiler() {
		return boiler;
	}

	@Override
	public FluidExtractable getInteractableExtractable() {
		if (getBoiler() != null) {
			return getBoiler().getOutputTank().getExtractable().getPureExtractable();
		}
		return EmptyFluidExtractable.NULL;
	}
}
