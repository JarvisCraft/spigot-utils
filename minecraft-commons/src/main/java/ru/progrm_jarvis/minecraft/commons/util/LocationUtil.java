package ru.progrm_jarvis.minecraft.commons.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LocationUtil {

    public void applyOffset(@NotNull Location location,
                            final double dx, final double dy, final double dz,
                            final float dYaw, final float dPitch) {
        location.add(dx, dy, dz).setYaw(location.getYaw() + dYaw);
        location.setPitch(location.getPitch() + dPitch);
    }

    public @NotNull Location withOffset(@NotNull Location location,
                                        final double dx, final double dy, final double dz,
                                        final float dYaw, final float dPitch) {
        (location = location.clone().add(dx, dy, dz)).setYaw(location.getYaw() + dYaw);
        location.setPitch(location.getPitch() + dPitch);

        return location;
    }

    public double getDistanceSquared(final double dx, final double dy, final double dz) {
        return dx * dx + dy * dy + dz * dz;
    }

    public double getDistanceSquared(final double x1, final double y1, final double z1,
                                     final double x2, final double y2, final double z2) {
        return getDistanceSquared(x2 - x1, y2 - y1, z2 - z1);
    }

    /**
     * <p>Gets the squared distance between the locations.</p>
     * <p>This is a world-ignoring analog of {@link Location#distanceSquared(Location)}.</p>
     *
     * @param location1 first location
     * @param location2 second location
     * @return the squared distance between the locations
     */
    public double getDistanceSquared(final Location location1, final Location location2) {
        return getDistanceSquared(
                location2.getX() - location1.getX(),
                location2.getY() - location1.getY(),
                location2.getZ() - location1.getZ()
        );
    }

    /**
     * <p>Gets the distance between the locations.</p>
     * <p>This is a world-ignoring analog of {@link Location#distance(Location)}.</p>
     *
     * @param location1 first location
     * @param location2 second location
     * @return the distance between the locations
     */
    public double getDistance(final Location location1, final Location location2) {
        return Math.sqrt(getDistanceSquared(location1, location2));
    }

    public Location nearestLocation(final @NonNull Location location, final @NonNull BlockFace blockFace) {
        return location.add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
    }
}
