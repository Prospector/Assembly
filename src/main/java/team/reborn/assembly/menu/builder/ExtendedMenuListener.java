package team.reborn.assembly.menu.builder;

import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import team.reborn.assembly.network.AssemblyNetworking;

public interface ExtendedMenuListener {

	public default void sendObject(ContainerListener listener, Container menu, int i, Object value) {
		if (listener instanceof ServerPlayerEntity) {
			AssemblyNetworking.syncContainer((ServerPlayerEntity) listener, i, value);
		}
	}

	public default void handleLong(int i, long value) {

	}

	public default void handleObject(int i, Object value) {

	}
}
