package teamreborn.assembly.container.builder;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import teamreborn.assembly.network.AssemblyNetworking;

public interface IExtendedContainerListener {

	public default void sendObject(ContainerListener containerListener, Container containerIn, int var, Object value) {
		if (containerListener instanceof ServerPlayerEntity) {
			AssemblyNetworking.syncContainer((ServerPlayerEntity) containerListener, var, value);
		}
	}

	public default void handleLong(int var, long value) {

	}

	public default void handleObject(int var, Object value) {

	}
}
