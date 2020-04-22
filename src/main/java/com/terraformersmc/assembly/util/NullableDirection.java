package com.terraformersmc.assembly.util;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.EnumMap;

public enum NullableDirection implements StringIdentifiable {
	NONE(null),
	DOWN(Direction.DOWN),
	UP(Direction.UP),
	NORTH(Direction.NORTH),
	SOUTH(Direction.SOUTH),
	WEST(Direction.WEST),
	EAST(Direction.EAST);

	public static final EnumMap<Direction, NullableDirection> DIRECTION_MAP = new EnumMap<>(Direction.class);
	final Direction direction;

	static {
		DIRECTION_MAP.put(Direction.DOWN, DOWN);
		DIRECTION_MAP.put(Direction.UP, UP);
		DIRECTION_MAP.put(Direction.NORTH, NORTH);
		DIRECTION_MAP.put(Direction.SOUTH, SOUTH);
		DIRECTION_MAP.put(Direction.WEST, WEST);
		DIRECTION_MAP.put(Direction.EAST, EAST);
	}

	NullableDirection(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public boolean hasDirection() {
		return this.direction != null;
	}

	public static NullableDirection get(Direction direction) {
		return DIRECTION_MAP.get(direction);
	}

	@Override
	public String asString() {
		return this.direction != null ? this.direction.asString() : "none";
	}
}
