package com.terraformersmc.assembly.util.math;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import javax.annotation.Nullable;

/**
 * This class is licensed under MPLv2 from AlexIIL's mod SimplePipes (https://github.com/AlexIIL/SimplePipes/blob/66819de5e7e48c87c6f18c278b2525de5e4bd74f/src/main/java/alexiil/mc/mod/pipes/util/ShapeUtil.java)
 */
public final class ShapeUtil {
	private ShapeUtil() {
	}

	public static VoxelShape cuboid16(int x0, int y0, int z0, int x1, int y1, int z1) {
		return VoxelShapes.cuboid(
				x0 / 16.0, y0 / 16.0, z0 / 16.0, //
				x1 / 16.0, y1 / 16.0, z1 / 16.0//
		);
	}

	public static VoxelShape cuboid(Vec3d a, Vec3d b) {
		return VoxelShapes.cuboid(a.x, a.y, a.z, b.x, b.y, b.z);
	}

	public static VoxelShape rotate90(VoxelShape shape, Direction from, Direction to) {
		return rotate90(shape, from, to, null);
	}

	public static VoxelShape rotate90(VoxelShape shape, Direction from, Direction to, @Nullable Axis rotationAxis) {
		if (from == to) {
			return shape;
		}
		VoxelShape result = VoxelShapes.empty();
		for (Box box : shape.getBoundingBoxes()) {
			Vec3d min = PositionUtil.rotateVec(new Vec3d(box.x1, box.y1, box.z1), from, to, rotationAxis);
			Vec3d max = PositionUtil.rotateVec(new Vec3d(box.x2, box.y2, box.z2), from, to, rotationAxis);
			result = VoxelShapes.combine(result, cuboid(min, max), BooleanBiFunction.OR);
		}
		return result.simplify();
	}
}
