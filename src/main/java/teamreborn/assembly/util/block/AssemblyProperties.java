package teamreborn.assembly.util.block;

import net.minecraft.state.property.BooleanProperty;

public class AssemblyProperties {
	public static final BooleanProperty ALIVE = BooleanProperty.of("alive");
	public static final BooleanProperty NORTH_LATEX = BooleanProperty.of("north_latex");
	public static final BooleanProperty SOUTH_LATEX = BooleanProperty.of("south_latex");
	public static final BooleanProperty WEST_LATEX = BooleanProperty.of("west_latex");
	public static final BooleanProperty EAST_LATEX = BooleanProperty.of("east_latex");
	public static final BooleanProperty POURING = BooleanProperty.of("pouring");
}
