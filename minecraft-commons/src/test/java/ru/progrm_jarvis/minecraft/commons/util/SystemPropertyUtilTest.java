package ru.progrm_jarvis.minecraft.commons.util;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemPropertyUtilTest {

    private static final String PREFIX = SystemPropertyUtilTest.class.getCanonicalName() + ".";

    @Test
    void testGetPropertyFunctional() {
        val propertyName = PREFIX + "prop1";

        System.clearProperty(propertyName);
        assertEquals("def", SystemPropertyUtil.<String>getSystemProperty(propertyName, t -> t, () -> "def"));

        System.setProperty(propertyName, "hi");
        assertEquals("hi", SystemPropertyUtil.<String>getSystemProperty(propertyName, t -> t, () -> "def"));

        System.clearProperty(propertyName);
        assertEquals("default", SystemPropertyUtil.<String>getSystemProperty(propertyName, t -> t, () -> "default"));
    }

    @Test
    void testGetProperty() {
        val propertyName = PREFIX + "prop2";

        System.clearProperty(propertyName);
        assertEquals("def", SystemPropertyUtil.getSystemProperty(propertyName, t -> t, "def"));

        System.setProperty(propertyName, "hi");
        assertEquals("hi", SystemPropertyUtil.getSystemProperty(propertyName, t -> t, "def"));

        System.clearProperty(propertyName);
        assertEquals("default", SystemPropertyUtil.getSystemProperty(propertyName, t -> t, "default"));
    }

    @Test
    void testGetPropertyIntFunctional() {
        val propertyName = PREFIX + "propInt1";

        System.clearProperty(propertyName);
        assertEquals(1232323, SystemPropertyUtil.getSystemPropertyInt(propertyName, () -> 1232323));

        System.setProperty(propertyName, "818475064");
        assertEquals(818_475_064, SystemPropertyUtil.getSystemPropertyInt(propertyName, () -> 1232323));

        System.clearProperty(propertyName);
        assertEquals(192855, SystemPropertyUtil.getSystemPropertyInt(propertyName, () -> 192855));

        System.setProperty(propertyName, "byaka");
        assertEquals(191919, SystemPropertyUtil.getSystemPropertyInt(propertyName, () -> 191919));

        System.clearProperty(propertyName);
        assertEquals(1118889, SystemPropertyUtil.getSystemPropertyInt(propertyName, () -> 1118889));
    }

    @Test
    void testGetPropertyInt() {
        val propertyName = PREFIX + "propInt2";

        System.clearProperty(propertyName);
        assertEquals(12321, SystemPropertyUtil.getSystemPropertyInt(propertyName, 12321));

        System.setProperty(propertyName, "45");
        assertEquals(45, SystemPropertyUtil.getSystemPropertyInt(propertyName, 12321));

        System.clearProperty(propertyName);
        assertEquals(45678, SystemPropertyUtil.getSystemPropertyInt(propertyName, 45678));

        System.setProperty(propertyName, "hello world");
        assertEquals(115553, SystemPropertyUtil.getSystemPropertyInt(propertyName, 115553));

        System.clearProperty(propertyName);
        assertEquals(154154153, SystemPropertyUtil.getSystemPropertyInt(propertyName, 154154153));
    }

    @Test
    void testGetPropertyLongFunctional() {
        val propertyName = PREFIX + "propLong1";

        System.clearProperty(propertyName);
        assertEquals(123232311, SystemPropertyUtil.getSystemPropertyLong(propertyName, () -> 123232311));

        System.setProperty(propertyName, "478623439");
        assertEquals(478_623_439, SystemPropertyUtil.getSystemPropertyLong(propertyName, () -> 123232311));

        System.clearProperty(propertyName);
        assertEquals(192333855, SystemPropertyUtil.getSystemPropertyLong(propertyName, () -> 192333855));

        System.setProperty(propertyName, "odin");
        assertEquals(272727276, SystemPropertyUtil.getSystemPropertyLong(propertyName, () -> 272727276));

        System.clearProperty(propertyName);
        assertEquals(167278389, SystemPropertyUtil.getSystemPropertyLong(propertyName, () -> 167278389));
    }

    @Test
    void testGetPropertyLong() {
        val propertyName = PREFIX + "propLong2";

        System.clearProperty(propertyName);
        assertEquals(1232112, SystemPropertyUtil.getSystemPropertyLong(propertyName, 1232112));

        System.setProperty(propertyName, "45666");
        assertEquals(45666, SystemPropertyUtil.getSystemPropertyLong(propertyName, 1232112));

        System.clearProperty(propertyName);
        assertEquals(4562278, SystemPropertyUtil.getSystemPropertyLong(propertyName, 4562278));

        System.setProperty(propertyName, "i am cool");
        assertEquals(379, SystemPropertyUtil.getSystemPropertyLong(propertyName, 379));

        System.clearProperty(propertyName);
        assertEquals(32678, SystemPropertyUtil.getSystemPropertyLong(propertyName, 32678));
    }

    @Test
    void testGetPropertyDoubleFunctional() {
        val propertyName = PREFIX + "propDouble1";

        System.clearProperty(propertyName);
        assertEquals(282.1, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 282.1));

        System.setProperty(propertyName, "1212.98756");
        assertEquals(1212.98756, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 282.1));

        System.clearProperty(propertyName);
        assertEquals(33435.2, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 33435.2));

        System.setProperty(propertyName, "2938");
        assertEquals(2938, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 282.1));

        System.clearProperty(propertyName);
        assertEquals(33435.2, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 33435.2));

        System.setProperty(propertyName, "broken");
        assertEquals(183617.22, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 183617.22));

        System.clearProperty(propertyName);
        assertEquals(3761.2374, SystemPropertyUtil.getSystemPropertyDouble(propertyName, () -> 3761.2374));
    }

    @Test
    void testGetPropertyDouble() {
        val propertyName = PREFIX + "propDouble2";

        System.clearProperty(propertyName);
        assertEquals(56347622.22, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 56347622.22));

        System.setProperty(propertyName, "48736487.143");
        assertEquals(48736487.143, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 56347622.143));

        System.clearProperty(propertyName);
        assertEquals(457646278.27, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 457646278.27));

        System.setProperty(propertyName, "130980");
        assertEquals(130980, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 33151.45));

        System.clearProperty(propertyName);
        assertEquals(21412441, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 21412441));

        System.setProperty(propertyName, "welcome");
        assertEquals(2141049.23241, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 2141049.23241));

        System.clearProperty(propertyName);
        assertEquals(117936, SystemPropertyUtil.getSystemPropertyDouble(propertyName, 117936));
    }

    @Test
    void testGetPropertyBooleanFunctional() {
        val propertyName = PREFIX + "propBoolean1";

        System.clearProperty(propertyName);
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> false));

        System.setProperty(propertyName, "true");
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> false));

        System.setProperty(propertyName, "true");
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> true));

        System.setProperty(propertyName, "false");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> true));

        System.setProperty(propertyName, "false");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> false));

        System.setProperty(propertyName, "wrong");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> true));

        System.setProperty(propertyName, "no");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> false));

        System.setProperty(propertyName, "petya");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> true));

        System.clearProperty(propertyName);
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> false));
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, () -> true));
    }

    @Test
    void testGetPropertyBoolean() {
        val propertyName = PREFIX + "propBoolean2";

        System.clearProperty(propertyName);
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));

        System.setProperty(propertyName, "true");
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));

        System.setProperty(propertyName, "false");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));

        System.setProperty(propertyName, "nope");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));

        System.setProperty(propertyName, "lel");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));

        System.clearProperty(propertyName);
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));
        assertTrue(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));

        System.setProperty(propertyName, "nah");
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, true));
        assertFalse(SystemPropertyUtil.getSystemPropertyBoolean(propertyName, false));
    }
}