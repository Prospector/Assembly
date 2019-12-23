package team.reborn.assembly.util.block;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import team.reborn.assembly.util.NullableDirection;

public class AssemblyProperties {
	// Hevea Log
	public static final BooleanProperty ALIVE = BooleanProperty.of("alive");
	public static final BooleanProperty NORTH_LATEX = BooleanProperty.of("north_latex");
	public static final BooleanProperty SOUTH_LATEX = BooleanProperty.of("south_latex");
	public static final BooleanProperty WEST_LATEX = BooleanProperty.of("west_latex");
	public static final BooleanProperty EAST_LATEX = BooleanProperty.of("east_latex");

	// Tree Tap
	public static final BooleanProperty POURING = BooleanProperty.of("pouring");

	// Tubes
	public static final EnumProperty<NullableDirection> CONNECTION_1 = EnumProperty.of("connection_1", NullableDirection.class);
	public static final EnumProperty<NullableDirection> CONNECTION_2 = EnumProperty.of("connection_2", NullableDirection.class);
}
