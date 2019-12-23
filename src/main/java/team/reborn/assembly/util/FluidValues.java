package team.reborn.assembly.util;

import reborncore.common.fluid.FluidValue;

public class FluidValues {
	public static final FluidValue BUCKET = FluidValue.BUCKET;
	public static final FluidValue HALF_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 2);
	public static final FluidValue QUARTER_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 4);
	public static final FluidValue FIFTH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 5);
	public static final FluidValue EIGHTH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 8);
	public static final FluidValue TENTH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 10);
	public static final FluidValue TWENTIETH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 20);
	public static final FluidValue FIFTIETH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 50);
	public static final FluidValue HUNDREDTH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 100);
	public static final FluidValue THOUSANDTH_BUCKET = FluidValue.fromRaw(FluidValue.BUCKET.getRawValue() / 1000);
	public static final FluidValue EMPTY = FluidValue.EMPTY;

}   
