package ru.progrm_jarvis.minecraft.commons.mojang;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MojangUtilTest {

    @Test
    void testFromMojangUuid() {
        assertEquals(
                UUID.fromString("29be10b1-b2d1-4130-b8f2-4fd14d3ccb62"),
                MojangUtil.fromMojangUuid("29be10b1b2d14130b8f24fd14d3ccb62")
        );
    }

    @Test
    void testToMojangUuid() {
        assertEquals(
                "29be10b1b2d14130b8f24fd14d3ccb62",
                MojangUtil.toMojangUuid(UUID.fromString("29be10b1-b2d1-4130-b8f2-4fd14d3ccb62"))
        );
    }
}