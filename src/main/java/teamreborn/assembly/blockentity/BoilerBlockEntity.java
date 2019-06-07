package teamreborn.assembly.blockentity;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import teamreborn.assembly.Assembly;
import teamreborn.assembly.container.builder.ContainerBuilder;

public class BoilerBlockEntity extends MachineBaseBlockEntity {
	public BoilerBlockEntity() {
		super(AssemblyBlockEntities.GRINDER);
	}

	@Override
	public Container createContainer(PlayerEntity playerEntity) {
		return (new ContainerBuilder(getId()))
			.player(playerEntity).inventory().hotbar().addInventory()
			.blockEntity(this).slot(0, 55, 45).outputSlot(1, 101, 45).syncCrafterValue()
			.addInventory().create(this, 0);
	}

	@Override
	public Identifier getId() {
		return new Identifier(Assembly.MOD_ID, "grinder");
	}

	@Override
	public int getInvSize() {
		return 3;
	}

	@Override
	public void tick() {

	}
}
