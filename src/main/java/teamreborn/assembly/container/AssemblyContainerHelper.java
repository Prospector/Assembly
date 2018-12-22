package teamreborn.assembly.container;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.gui.GuiProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import teamreborn.assembly.blockentity.MachineBaseBlockEntity;
import teamreborn.assembly.client.gui.GrinderGui;

public class AssemblyContainerHelper implements ModInitializer {

	@Override
	public void onInitialize() {
		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier("assembly", "grinder"), (identifier, playerEntity, packetByteBuf) -> {
			BlockPos pos = packetByteBuf.readBlockPos();
			BlockEntity blockEntity = playerEntity.world.getBlockEntity(pos);
			if (blockEntity instanceof MachineBaseBlockEntity) {
				return ((MachineBaseBlockEntity) blockEntity).createContainer(playerEntity);
			}
			return null;
		});

		GuiProviderRegistry.INSTANCE.registerFactory(new Identifier("assembly", "grinder"), GrinderGui::new);

	}

}
