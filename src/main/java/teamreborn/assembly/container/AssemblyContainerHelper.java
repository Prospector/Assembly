package teamreborn.assembly.container;

import teamreborn.assembly.blockentity.MachineBaseBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class AssemblyContainerHelper implements ModInitializer {

	@Override
	public void onInitialize() {
		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier("assembly", "grinder"), (syncId, id, player, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			BlockEntity blockEntity = player.world.getBlockEntity(pos);
			if (blockEntity instanceof MachineBaseBlockEntity) {
				return ((MachineBaseBlockEntity) blockEntity).createContainer(player);
			}
			return null;
		});

	}

}
