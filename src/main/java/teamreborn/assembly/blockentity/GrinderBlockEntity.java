package teamreborn.assembly.blockentity;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import teamreborn.assembly.container.builder.ContainerBuilder;
import teamreborn.assembly.container.FabricContainerProvider;
import teamreborn.assembly.registry.AssemblyBlockEntities;

public class GrinderBlockEntity extends MachineBaseBlockEntity implements FabricContainerProvider {
	public GrinderBlockEntity() {
		super(AssemblyBlockEntities.GRINDER);
	}


	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return (new ContainerBuilder("grinder"))
			.player(playerEntity).inventory().hotbar().addInventory()
			.blockEntity(this).slot(0, 55, 45).outputSlot(1, 101, 45)
			.addInventory().create(this);
	}

	@Override
	public Identifier getContainerIdentifier() {
		return new Identifier("assembly", "grinder");
	}

	@Override
	public int getInvSize() {
		return 3;
	}
}
