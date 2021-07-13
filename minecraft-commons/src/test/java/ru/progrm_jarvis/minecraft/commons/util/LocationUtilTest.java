package ru.progrm_jarvis.minecraft.commons.util;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.progrm_jarvis.minecraft.commons.util.LocationUtil.getDistanceSquared;

class LocationUtilTest {

    @Test
    void testGetDistanceSquaredFromDelta() {
        assertEquals(0, getDistanceSquared(0, 0, 0));

        assertEquals(9 * 3, getDistanceSquared(3, 3, 3));
        assertEquals(25 * 3, getDistanceSquared(5, 5, 5));
        assertEquals(2.25 * 3, getDistanceSquared(1.5, 1.5, 1.5));
        assertEquals(2.25 * 2 + 0.25, getDistanceSquared(1.5, 0.5, 1.5));
        assertEquals(2.25 * 2 + 0.25, getDistanceSquared(1.5, 0.5, 1.5));

        assertEquals(9 * 3, getDistanceSquared(-3, 3, 3));
        assertEquals(25 * 3, getDistanceSquared(5, -5, 5));
        assertEquals(2.25 * 3, getDistanceSquared(1.5, 1.5, -1.5));
        assertEquals(2.25 * 2 + 0.25, getDistanceSquared(-1.5, 0.5, -1.5));
        assertEquals(2.25 * 2 + 0.25, getDistanceSquared(-1.5, -0.5, -1.5));
    }

    @Test
    strictfp void testGetDistanceSquaredFromCoordinates() {
        assertEquals(9 * 3, getDistanceSquared(0, 0, 0, 3, 3, 3));
        assertEquals(9 * 3, getDistanceSquared(3, 3, 3, 0, 0, 0));

        assertEquals(9 * 3, getDistanceSquared(0, 0, 0, -3, -3, -3));
        assertEquals(9 * 3, getDistanceSquared(-3, -3, -3, 0, 0, 0));

        assertEquals(9 * 3, getDistanceSquared(0, 0, 0, 3, -3, -3));
        assertEquals(9 * 3, getDistanceSquared(3, -3, -3, 0, 0, 0));
        assertEquals(9 * 3, getDistanceSquared(3, -3, -3, 0, 0, 0));

        assertEquals(9 * 3, getDistanceSquared(0, 0, 0, 3, 3, 3));
        assertEquals(9 * 3, getDistanceSquared(3, 3, 3, 0, 0, 0));

        assertEquals(9 * 3, getDistanceSquared(-1.5, -1.5, -1.5, 1.5, 1.5, 1.5));
        assertEquals(9 * 3, getDistanceSquared(1.5, 1.5, 1.5, -1.5, -1.5, -1.5));

        assertEquals(9 * 2, getDistanceSquared(-1.5, 0, -1.5, 1.5, 0, 1.5));
        assertEquals(9, getDistanceSquared(1, 1.5, -1, 1, -1.5, -1));

        assertEquals(2.25, getDistanceSquared(1.5, 1, 0, 0, 1, 0));
        assertEquals(2.25 + 9, getDistanceSquared(1.5, 1, 1.5, 0, 1, -1.5));

        assertEquals(1 + 4 + 9, getDistanceSquared(1, 2, 3, 0, 0, 0));
        assertEquals(0.25 + 0.04 + 0.09, getDistanceSquared(0.5, 0.1, 0.2, 0, 0.3, -0.1));
    }

    @Test
    void testGetDistanceSquaredFromLocations() {
        assertEquals(9 * 3, getDistanceSquared(location(0, 0, 0), location(3, 3, 3)));
        assertEquals(9 * 3, getDistanceSquared(location(3, 3, 3), location(0, 0, 0)));

        assertEquals(9 * 3, getDistanceSquared(location(0, 0, 0), location(-3, -3, -3)));
        assertEquals(9 * 3, getDistanceSquared(location(-3, -3, -3), location(0, 0, 0)));

        assertEquals(9 * 3, getDistanceSquared(location(0, 0, 0), location(3, -3, -3)));
        assertEquals(9 * 3, getDistanceSquared(location(3, -3, -3), location(0, 0, 0)));
        assertEquals(9 * 3, getDistanceSquared(location(3, -3, -3), location(0, 0, 0)));

        assertEquals(9 * 3, getDistanceSquared(location(0, 0, 0), location(3, 3, 3)));
        assertEquals(9 * 3, getDistanceSquared(location(3, 3, 3), location(0, 0, 0)));

        assertEquals(9 * 3, getDistanceSquared(location(-1.5, -1.5, -1.5), location(1.5, 1.5, 1.5)));
        assertEquals(9 * 3, getDistanceSquared(location(1.5, 1.5, 1.5), location(-1.5, -1.5, -1.5)));

        assertEquals(9 * 2, getDistanceSquared(location(-1.5, 0, -1.5), location(1.5, 0, 1.5)));
        assertEquals(9, getDistanceSquared(location(1, 1.5, -1), location(1, -1.5, -1)));

        assertEquals(2.25, getDistanceSquared(location(1.5, 1, 0), location(0, 1, 0)));
        assertEquals(2.25 + 9, getDistanceSquared(location(1.5, 1, 1.5), location(0, 1, -1.5)));

        assertEquals(1 + 4 + 9, getDistanceSquared(location(1, 2, 3), location(0, 0, 0)));
        assertEquals(0.25 + 0.04 + 0.09, getDistanceSquared(location(0.5, 0.1, 0.2), location(0, 0.3, -0.1)));
    }

    private static Location location(double x, double y, double z) {
        return new Location(null, x, y, z);
    }
}