package team.reborn.assembly.util;

import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import com.google.common.collect.ImmutableSet;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import team.reborn.assembly.block.enums.ValveState;
import team.reborn.assembly.tags.AssemblyFluidTags;

import java.util.Set;

public class AssemblyConstants {
	public static final String MOD_ID = "assembly";
	public static final String COMMON_NAMESPACE = "c";
	public static final Set<String> AUTHORS = ImmutableSet.of("9927b75a-89d9-4ff7-8bac-58aaed08911a");

	public static class NbtKeys {
		public static final String FLUIDS = "Fluids";
		public static final String INPUT_FLUIDS = "InputFluids";
		public static final String OUTPUT_FLUIDS = "OutputFluids";

		public static final String RECIPE = "Recipe";

		//Fluid Hopper & Spigot
		public static final String TRANSFER_COOLDOWN = "TransferCooldown";
		public static final String CUSTOM_NAME = "CustomName";

		//Spigot
		public static final String POURING_FLUID = "PouringFluid";

		//Steam Press
		public static final String BURN_TIME = "BurnTime";
		public static final String PRESS_PROGRESS = "Progress";
		public static final String PRESS_RESET = "Reset";
		public static final String CURRENT_PRESSES = "CurrentPresses";
		public static final String MASTER = "Master";

		//Exoframe
		public static final String MODULE = "Module";
	}

	public static class Ids {
		public static final Identifier BOILER = id("boiler");
		public static final Identifier BOILER_CHAMBER = id("boiler_chamber");
		public static final Identifier STEAM_PRESS = id("steam_press");

		public static final Identifier AUTHOR_CAPE = id("textures/entity/player/author_cape.png");
		public static final Identifier CONTRIBUTOR_CAPE = id("textures/entity/player/contributor_cape.png");
	}

	public static class Properties {
		// Hevea Log
		public static final BooleanProperty ALIVE = BooleanProperty.of("alive");
		public static final BooleanProperty NORTH_LATEX = BooleanProperty.of("north_latex");
		public static final BooleanProperty SOUTH_LATEX = BooleanProperty.of("south_latex");
		public static final BooleanProperty WEST_LATEX = BooleanProperty.of("west_latex");
		public static final BooleanProperty EAST_LATEX = BooleanProperty.of("east_latex");

		// Tree Tap & Spigot
		public static final BooleanProperty POURING = BooleanProperty.of("pouring");

		// Spigot
		public static final EnumProperty<ValveState> VALVE = EnumProperty.of("valve", ValveState.class);
		public static final IntProperty EXTENSION = IntProperty.of("extension", 0, 4);
	}

	public static class FluidFilters {
		public static final FluidFilter STEAM = fluidKey -> {
			Fluid rawFluid = fluidKey.getRawFluid();
			return rawFluid != null && rawFluid.matches(AssemblyFluidTags.STEAM);
		};
	}

	private static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
