package com.terraformersmc.assembly.client.screen;

import com.terraformersmc.assembly.blockentity.base.AssemblyContainerBlockEntity;
import com.terraformersmc.assembly.client.screen.base.BaseSyncedScreen;
import com.terraformersmc.assembly.screen.AssemblyScreenSyncers;
import com.terraformersmc.assembly.screen.builder.ScreenSyncer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class AssemblySyncedScreens {
	public static final Set<Pair<Identifier, Function<ScreenSyncer, BaseSyncedScreen>>> SCREENS = new HashSet<>();

	public static void register() {
		add(AssemblyScreenSyncers.BOILER, BoilerScreen::new);
		add(AssemblyScreenSyncers.BOILER_CHAMBER, BaseSyncedScreen::new);
		add(AssemblyScreenSyncers.FLUID_HOPPER, BaseSyncedScreen::new);
		add(AssemblyScreenSyncers.TINKERING_TABLE, TinkeringTableScreen::new);
		add(AssemblyScreenSyncers.INJECTOR, InjectorScreen::new);
		add(AssemblyScreenSyncers.SQUEEZER, SqueezerScreen::new);

		for (Pair<Identifier, Function<ScreenSyncer, BaseSyncedScreen>> screen : SCREENS) {
			ScreenProviderRegistry.INSTANCE.registerFactory(screen.getLeft(), screenHandler -> {
				if (screenHandler instanceof ScreenSyncer) {
					return screen.getRight().apply((ScreenSyncer) screenHandler);
				}
				return null;
			});
		}
	}

	private static <BE extends AssemblyContainerBlockEntity & Nameable> void add(Identifier screenSyncer, Function<ScreenSyncer<BE>, BaseSyncedScreen<BE>> screen) {
		SCREENS.add(new Pair(screenSyncer, screen));
	}
}
