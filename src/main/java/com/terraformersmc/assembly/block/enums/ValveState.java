package com.terraformersmc.assembly.block.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Random;

public enum ValveState implements StringIdentifiable {
	OPEN("open", true),
	CLOSED_LEFT("closed_left", false),
	CLOSED_RIGHT("closed_right", false);

	public String name;
	public boolean open;

	ValveState(String name, boolean open) {
		this.name = name;
		this.open = open;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public boolean isOpen() {
		return this.open;
	}

	public static ValveState randomClosed(Random random) {
		return random.nextInt(2) == 0 ? CLOSED_LEFT : CLOSED_RIGHT;
	}
}
