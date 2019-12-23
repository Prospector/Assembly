package team.reborn.assembly.util;

import net.minecraft.client.texture.Sprite;

public class MathUtil {
	public static double fracd(double d) {
		return (double) 1 / (double) 16 * d;
	}

	public static float fracf(double d) {
		return (float) 1 / (float) 16 * (float) d;
	}

	public static float frac(float d) {
		return (float) 1 / (float) 16 * d;
	}

	public static boolean isInRange(int point, int min, int max) {
		return point >= min && point <= max;
	}

	public static boolean isInRect(int pointX, int pointY, int rectMinX, int rectMinY, int rectMaxX, int rectMaxY) {
		return pointX >= rectMinX && pointX <= rectMaxX && pointY >= rectMinY && pointY <= rectMaxY;
	}

	public static boolean isInCuboid(int pointX, int pointY, int pointZ, int rectMinX, int rectMinY, int rectMinZ, int rectMaxX, int rectMaxY, int rectMaxZ) {
		return pointX >= rectMinX && pointX <= rectMaxX && pointY >= rectMinY && pointY <= rectMaxY && pointZ >= rectMinZ && pointZ <= rectMaxZ;
	}


	public static float getXFromU(Sprite sprite, float f) {
		float g = sprite.getMaxU() - sprite.getMinU();
		return (f - sprite.getMinU()) / g * 16.0F;
	}

	public static float getYFromV(Sprite sprite, float f) {
		float g = sprite.getMaxV() - sprite.getMinV();
		return (f - sprite.getMinV()) / g * 16.0F;
	}
}
