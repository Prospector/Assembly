package com.terraformersmc.assembly.screen.builder;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import com.terraformersmc.assembly.networking.AssemblyNetworking;

public interface ExtendedScreenHandlerListener {

	default void sendObject(ScreenHandlerListener listener, ScreenHandler screenHandler, int i, Object value) {
		if (listener instanceof ServerPlayerEntity) {
			AssemblyNetworking.syncScreenHandler((ServerPlayerEntity) listener, i, value);
		}
	}

	default void handleLong(int i, long value) {

	}

	default void handleObject(int index, Object value) {

	}
}
