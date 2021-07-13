package ru.progrm_jarvis.minecraft.commons.math.dimensional;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * A figure existing in 3-dimensional system.
 */
public interface Figure3D {

    /**
     * A singleton pseudo-figure not containing any possible points of a 3D-space.
     */
    Figure3D EMPTY = new Figure3D() {
        //<editor-fold desc="Empty pseudo-figure singleton class" defaultstate="collapsed">

        @Override
        public double getMinX() {
            return Double.NaN;
        }

        @Override
        public double getMaxX() {
            return Double.NaN;
        }

        @Override
        public double getMinY() {
            return Double.NaN;
        }

        @Override
        public double getMaxY() {
            return Double.NaN;
        }

        @Override
        public double getMinZ() {
            return Double.NaN;
        }

        @Override
        public double getMaxZ() {
            return Double.NaN;
        }

        @Override
        public boolean contains(final double x, final double y, final double z) {
            return false;
        }

        @Override
        public boolean contains(final @NonNull Vector point) {
            return false;
        }

        @Override
        public boolean contains(final @NonNull Location point) {
            return false;
        }
        //</editor-fold>
    };

    /**
     * A singleton pseudo-figure containing all possible points of a 3D-space.
     */
    Figure3D WHOLE = new Figure3D() {
        //<editor-fold desc="Whole pseudo-figure singleton class" defaultstate="collapsed">

        @Override
        public double getMinX() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double getMaxX() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double getMinY() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double getMaxY() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double getMinZ() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double getMaxZ() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public boolean contains(final double x, final double y, final double z) {
            return true;
        }

        @Override
        public boolean contains(final @NonNull Vector point) {
            return true;
        }

        @Override
        public boolean contains(final @NonNull Location point) {
            return true;
        }
        //</editor-fold>
    };

    /**
     * Gets the minimal figure's coordinate by X axis.
     *
     * @return the least X-coordinate of the figure
     */
    double getMinX();

    /**
     * Gets the maximal figure's coordinate by X axis.
     *
     * @return the most X-coordinate of the figure
     */
    double getMaxX();

    /**
     * Gets the minimal figure's coordinate by Y axis.
     *
     * @return the least Y-coordinate of the figure
     */
    double getMinY();

    /**
     * Gets the maximal figure's coordinate by Y axis.
     *
     * @return the most Y-coordinate of the figure
     */
    double getMaxY();

    /**
     * Gets the minimal figure's coordinate by Z axis.
     *
     * @return the least Z-coordinate of the figure
     */
    double getMinZ();

    /**
     * Gets the maximal figure's coordinate by Z axis.
     *
     * @return the most Z-coordinate of the figure
     */
    double getMaxZ();

    /**
     * Checks whether or not this figure contains the point.
     *
     * @param x X coordinate of a point to check
     * @param y Y coordinate of a point to check
     * @param z Z coordinate of a point to check
     *
     * @return {@code true} if the point belongs to this figure and {@code false} otherwise
     */
    boolean contains(double x, double y, double z);

    /**
     * Checks whether or not this figure contains the point.
     *
     * @param point point to check
     *
     * @return {@code true} if the point belongs to this figure and {@code false} otherwise
     */
    default boolean contains(final @NonNull Vector point) {
        return contains(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Checks whether or not this figure contains the point.
     *
     * @param point point to check
     *
     * @return {@code true} if the point belongs to this figure and {@code false} otherwise
     */
    default boolean contains(final @NonNull Location point) {
        return contains(point.getX(), point.getY(), point.getZ());
    }
}
