package com.terraformersmc.assembly.sound;

import com.terraformersmc.assembly.Assembly;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssemblySoundEvents {

	private static final Map<Identifier, SoundEvent> SOUNDS = new LinkedHashMap<>();

	public static final SoundEvent SPIGOT_OPEN = add("block.spigot.open");
	public static final SoundEvent SPIGOT_CLOSE = add("block.spigot.close");

	private static SoundEvent add(String id) {
		Identifier identifier = new Identifier(Assembly.MOD_ID, id);
		SoundEvent event = new SoundEvent(identifier);
		SOUNDS.put(identifier, event);
		return event;
	}

	public static void register() {
		for (Identifier identifier : SOUNDS.keySet()) {
			Registry.register(Registry.SOUND_EVENT, identifier, SOUNDS.get(identifier));
		}
	}
}
