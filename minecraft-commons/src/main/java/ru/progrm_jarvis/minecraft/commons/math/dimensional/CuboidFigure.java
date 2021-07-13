package ru.progrm_jarvis.minecraft.commons.math.dimensional;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * A cuboid figure.
 */
@Value
@FieldDefaults(level = AccessLevel.PROTECTED)
@NonFinal public class CuboidFigure implements Figure3D {

    @Getter double minX, minY, minZ, maxX, maxY, maxZ;

    public CuboidFigure(final double x1, final double y1, final double z1,
                        final double x2, final double y2, final double z2) {
        if (x1 < x2) {
            minX = x1;
            maxX = x2;
        } else {
            minX = x2;
            maxX = x1;
        }

        if (y1 < y2) {
            minY = y1;
            maxY = y2;
        } else {
            minY = y2;
            maxY = y1;
        }

        if (z1 < z2) {
            minZ = z1;
            maxZ = z2;
        } else {
            minZ = z2;
            maxZ = z1;
        }
    }

    public CuboidFigure(final double x, final double y, final double z) {
        this(0, 0, 0, x, y, z);
    }

    @Override
    public boolean contains(final double x, final double y, final double z) {
        return minX <= x && x <= maxX
                && minY <= y && y <= maxY
                && minZ <= z && z <= maxZ;
    }

    /**
     * Creates a cubic figure based on two points.
     *
     * @param x1 X-coordinate of the first point of the cubic figure
     * @param y1 T-coordinate of the first point of the cubic figure
     * @param z1 Z-coordinate of the first point of the cubic figure
     * @param x2 X-coordinate of the second point of the cubic figure
     * @param y2 T-coordinate of the second point of the cubic figure
     * @param z2 Z-coordinate of the second point of the cubic figure
     * @return created cubic figure including the area between the two points
     */
    @NonNull
    private static Figure3D between(final double x1, final double y1, final double z1,
                                    final double x2, final double y2, final double z2) {
        if (x1 == x2 && y1 == y2 && z1 == z2) return new PointFigure(x1, y1, z1);
        return new CuboidFigure(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Creates a cubic figure based on two points.
     *
     * @param point1 the first point of the cubic figure
     * @param point2 the second point of the cubic figure
     * @return created cubic figure including the area between the two points
     */
    public static Figure3D between(final @NonNull Vector point1, final @NonNull Vector point2) {
        return between(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
    }

    /**
     * Creates a cubic figure based on two points.
     *
     * @param point1 the first point of the cubic figure
     * @param point2 the second point of the cubic figure
     * @return created cubic figure including the area between the two points
     */
    public static Figure3D between(final @NonNull Location point1, final @NonNull Location point2) {
        return between(point1.getX(), point1.getY(), point1.getZ(), point2.getX(), point2.getY(), point2.getZ());
    }
}
