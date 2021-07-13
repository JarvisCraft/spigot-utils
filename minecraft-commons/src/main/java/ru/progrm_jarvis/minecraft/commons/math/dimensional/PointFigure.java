package ru.progrm_jarvis.minecraft.commons.math.dimensional;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * A point figure covering only one exact coordinate.
 */
@Value
@FieldDefaults(level = AccessLevel.PROTECTED)
@NonFinal public class PointFigure implements Figure3D {

    double x, y, z;

    //<editor-fold desc="Simple methods" defaultstate="collapsed">
    @Override
    public double getMinX() {
        return x;
    }

    @Override
    public double getMaxX() {
        return x;
    }

    @Override
    public double getMinY() {
        return y;
    }

    @Override
    public double getMaxY() {
        return y;
    }

    @Override
    public double getMinZ() {
        return z;
    }

    @Override
    public double getMaxZ() {
        return z;
    }
    //</editor-fold>

    @Override
    public boolean contains(final double x, final double y, final double z) {
        return x == this.x && y == this.y && z == this.z;
    }

    /**
     * Converts the {@link Vector} representation to a {@link PointFigure} representation.
     *
     * @param point vector representation to convert
     * @return point figure identical to the specified vector point
     */
    public static PointFigure from(final @NonNull Vector point) {
        return new PointFigure(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Converts the {@link Location} representation to a {@link PointFigure} representation.
     *
     * @param point location representation to convert
     * @return point figure identical to the specified location point
     */
    public static PointFigure from(final @NonNull Location point) {
        return new PointFigure(point.getX(), point.getY(), point.getZ());
    }
}
