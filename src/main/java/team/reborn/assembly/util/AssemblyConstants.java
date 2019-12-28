package team.reborn.assembly.util;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

public class AssemblyConstants {
	public static final String MOD_ID = "assembly";

	public static class NbtKeys {
		public static final String FLUIDS = "Fluids";
		public static final String INPUT_FLUIDS = "InputFluids";
		public static final String OUTPUT_FLUIDS = "OutputFluids";

		public static final String BURN_TIME = "BurnTime";
		public static final String PRESS_PROGRESS = "Progress";
		public static final String PRESS_RESET = "Reset";
		public static final String RECIPE = "Recipe";
		public static final String CURRENT_PRESSES = "CurrentPresses";
	}

	public static class Ids {
		public static final Identifier BOILER = id("boiler");
		public static final Identifier BOILER_CHAMBER = id("boiler_chamber");
		public static final Identifier STEAM_PRESS = id("steam_press");
	}

	public static class Properties {
		// Hevea Log
		public static final BooleanProperty ALIVE = BooleanProperty.of("alive");
		public static final BooleanProperty NORTH_LATEX = BooleanProperty.of("north_latex");
		public static final BooleanProperty SOUTH_LATEX = BooleanProperty.of("south_latex");
		public static final BooleanProperty WEST_LATEX = BooleanProperty.of("west_latex");
		public static final BooleanProperty EAST_LATEX = BooleanProperty.of("east_latex");

		// Tree Tap
		public static final BooleanProperty POURING = BooleanProperty.of("pouring");
	}

	private static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
