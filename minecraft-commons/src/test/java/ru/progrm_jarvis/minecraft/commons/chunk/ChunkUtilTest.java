package ru.progrm_jarvis.minecraft.commons.chunk;

import lombok.val;
import lombok.var;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.progrm_jarvis.minecraft.commons.chunk.ChunkUtil.*;

class ChunkUtilTest {

    @Test
    void testRangeCheckChunkLocalX() {
        for (var chunkLocalX = -32; chunkLocalX <= 32; chunkLocalX++) {
            final Executable executable;
            {
                val x = chunkLocalX;
                executable = () -> ChunkUtil.rangeCheckChunkLocalX(x);
            }

            if (chunkLocalX < 0 || chunkLocalX > 15) assertThrows(IllegalArgumentException.class, executable);
            else assertDoesNotThrow(executable);
        }
    }

    @Test
    void testRangeCheckChunkLocalY() {
        for (var chunkLocalZ = -32; chunkLocalZ <= 32; chunkLocalZ++) {
            final Executable executable;
            {
                val z = chunkLocalZ;
                executable = () -> ChunkUtil.rangeCheckChunkLocalZ(z);
            }

            if (chunkLocalZ < 0 || chunkLocalZ > 15) assertThrows(IllegalArgumentException.class, executable);
            else assertDoesNotThrow(executable);
        }
    }

    @Test
    void testRangeCheckChunkLocalZ() {
        for (var chunkLocalY = -512; chunkLocalY <= 512; chunkLocalY++) {
            final Executable executable;
            {
                val y = chunkLocalY;
                executable = () -> ChunkUtil.rangeCheckChunkLocalY(y);
            }

            if (chunkLocalY < 0 || chunkLocalY > 255) assertThrows(IllegalArgumentException.class, executable);
            else assertDoesNotThrow(executable);
        }
    }

    @Test
    void testRangeCheckChunkLocal() {
        for (var chunkLocalY = -256; chunkLocalY <= 256; chunkLocalY++) {
            val y = chunkLocalY;
            for (var chunkLocalX = -32; chunkLocalX <= 32; chunkLocalX++) {
                val x = chunkLocalX;
                for (var chunkLocalZ = -32; chunkLocalZ <= 32; chunkLocalZ++) {
                    final Executable executable;
                    {
                        final int z = chunkLocalZ;
                        executable = () -> ChunkUtil.rangeCheckChunkLocal(x, y, z);
                    }

                    if (chunkLocalX < 0 || chunkLocalX > 15
                            || chunkLocalZ < 0 || chunkLocalZ > 15
                            || chunkLocalY < 0 || chunkLocalY > 255) assertThrows(
                                    IllegalArgumentException.class, executable
                    );
                    else assertDoesNotThrow(executable);
                }
            }
        }
    }

    @Test
    void testToChunkLong() {
        byte signs = 0;
        for (var i = 0; i < 128 + RandomUtils.nextInt(128) ; i++) {
            final int x, z;
            // make sure all sign combinations happen
            switch (signs++ % 4) {
                case 0: {
                    x = RandomUtils.nextInt();
                    z = RandomUtils.nextInt();

                    break;
                }
                case 1: {
                    x = -RandomUtils.nextInt();
                    z = RandomUtils.nextInt();

                    break;
                }
                case 2: {
                    x = RandomUtils.nextInt();
                    z = -RandomUtils.nextInt();

                    break;
                }
                case 3: /* math doesn't work here :) */ default: {
                    x = -RandomUtils.nextInt();
                    z = -RandomUtils.nextInt();

                    break;
                }
            }

            val chunk = toChunkLong(x, z);
            assertEquals(x, chunkX(chunk));
            assertEquals(z, chunkZ(chunk));
        }
    }

    @Test
    void testChunkByLocation() {
        long chunk;

        chunk = chunkAt(0, 0);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkAt(15, 15);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkAt(16, 16);
        assertEquals(1, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkAt(31, 31);
        assertEquals(1, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkAt(32, 32);
        assertEquals(2, chunkX(chunk));
        assertEquals(2, chunkZ(chunk));

        chunk = chunkAt(47, 47);
        assertEquals(2, chunkX(chunk));
        assertEquals(2, chunkZ(chunk));

        chunk = chunkAt(-1, -1);
        assertEquals(-1, chunkX(chunk));
        assertEquals(-1, chunkZ(chunk));

        chunk = chunkAt(-16, -16);
        assertEquals(-1, chunkX(chunk));
        assertEquals(-1, chunkZ(chunk));

        chunk = chunkAt(-17, -17);
        assertEquals(-2, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkAt(-32, -32);
        assertEquals(-2, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkAt(-33, -33);
        assertEquals(-3, chunkX(chunk));
        assertEquals(-3, chunkZ(chunk));

        chunk = chunkAt(-48, -48);
        assertEquals(-3, chunkX(chunk));
        assertEquals(-3, chunkZ(chunk));

        chunk = chunkAt(0, 15);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkAt(-1, 15);
        assertEquals(-1, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkAt(0, 15);
        assertEquals(0, chunkX(chunk));
        assertEquals(0, chunkZ(chunk));

        chunk = chunkAt(-31, 31);
        assertEquals(-2, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkAt(64, -32);
        assertEquals(4, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkAt(10, 20);
        assertEquals(0, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkAt(-10, 20);
        assertEquals(-1, chunkX(chunk));
        assertEquals(1, chunkZ(chunk));

        chunk = chunkAt(10, -20);
        assertEquals(0, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));

        chunk = chunkAt(-10, -20);
        assertEquals(-1, chunkX(chunk));
        assertEquals(-2, chunkZ(chunk));
    }

    @Test
    void testGetChunkFromWorld() {
        val world = mock(World.class);

        getChunk(world, toChunkLong(0, 0));
        verify(world).getChunkAt(ArgumentMatchers.eq(0), ArgumentMatchers.eq(0));

        getChunk(world, toChunkLong(1, 0));
        verify(world).getChunkAt(ArgumentMatchers.eq(1), ArgumentMatchers.eq(0));

        getChunk(world, toChunkLong(0, 1));
        verify(world).getChunkAt(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1));

        getChunk(world, toChunkLong(-1, 0));
        verify(world).getChunkAt(ArgumentMatchers.eq(-1), ArgumentMatchers.eq(0));

        getChunk(world, toChunkLong(0, -1));
        verify(world).getChunkAt(ArgumentMatchers.eq(0), ArgumentMatchers.eq(-1));

        getChunk(world, toChunkLong(25, 10));
        verify(world).getChunkAt(ArgumentMatchers.eq(25), ArgumentMatchers.eq(10));

        getChunk(world, toChunkLong(-14, 8));
        verify(world).getChunkAt(ArgumentMatchers.eq(-14), ArgumentMatchers.eq(8));

        getChunk(world, toChunkLong(23, -9));
        verify(world).getChunkAt(ArgumentMatchers.eq(23), ArgumentMatchers.eq(-9));

        getChunk(world, toChunkLong(-6, -22));
        verify(world).getChunkAt(ArgumentMatchers.eq(-6), ArgumentMatchers.eq(-22));
    }

    @Test
    void testToChunkLocalLocation() {
        assertDoesNotThrow(() -> toChunkLocalLocationShort(0, 10, 10));
        assertDoesNotThrow(() -> toChunkLocalLocationShort(15, 10, 10));
        assertThrows(IllegalArgumentException.class, () -> toChunkLocalLocationShort(-1, 10, 10));
        assertThrows(IllegalArgumentException.class, () -> toChunkLocalLocationShort(16, 10, 10));

        assertDoesNotThrow(() -> toChunkLocalLocationShort(10, 0, 10));
        assertDoesNotThrow(() -> toChunkLocalLocationShort(10, 255, 10));
        assertThrows(IllegalArgumentException.class, () -> toChunkLocalLocationShort(10, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> toChunkLocalLocationShort(10, 256, 10));

        assertDoesNotThrow(() -> toChunkLocalLocationShort(10, 10, 0));
        assertDoesNotThrow(() -> toChunkLocalLocationShort(10, 10, 15));
        assertThrows(IllegalArgumentException.class, () -> toChunkLocalLocationShort(10, 10, -1));
        assertThrows(IllegalArgumentException.class, () -> toChunkLocalLocationShort(10, 10, 16));

        for (var y = 0; y < 255; y++) for (var x = 0; x < 15; x++) for (var z = 0; z < 15; z++) {
            val chunkLocalLocation = toChunkLocalLocationShort(x, y, z);

            assertEquals(x, chunkLocalLocationX(chunkLocalLocation));
            assertEquals(y, chunkLocalLocationY(chunkLocalLocation));
            assertEquals(z, chunkLocalLocationZ(chunkLocalLocation));
        }
    }

    @Test
    void testChunkLocalLocationFromLocation() {
        val world = mock(World.class);

        byte signs = 0;
        for (var i = 0; i < 128 + RandomUtils.nextInt(129); i++) {
            final int x, z,
                    y = RandomUtils.nextInt(256), // y is the same for location and chunk-local location
                    chunkLocalX = RandomUtils.nextInt(16),
                    chunkLocalZ = RandomUtils.nextInt(16);
            // coordinates are generating by adding random amount of chunks to chunk-local coordinates;
            // make sure all sign combinations happen
            switch (signs++ % 4) {
                case 0: {
                    x = chunkLocalX + 16 * RandomUtils.nextInt();
                    z = chunkLocalZ + 16 * RandomUtils.nextInt();

                    break;
                }
                case 1: {
                    x = chunkLocalX + 16 * -RandomUtils.nextInt();
                    z = chunkLocalZ + 16 * RandomUtils.nextInt();

                    break;
                }
                case 2: {
                    x = chunkLocalX + 16 * RandomUtils.nextInt();
                    z = chunkLocalZ + 16 * -RandomUtils.nextInt();

                    break;
                }
                case 3: /* math doesn't work here :) */ default: {
                    x = chunkLocalX + 16 * -RandomUtils.nextInt();
                    z = chunkLocalZ + 16 * -RandomUtils.nextInt();

                    break;
                }
            }

            val location = new Location(world, x, y, z);
            val chunkLocalLocation = ChunkUtil.chunkLocalLocation(location);

            assertEquals(chunkLocalX, chunkLocalLocationX(chunkLocalLocation));
            assertEquals(y, chunkLocalLocationY(chunkLocalLocation));
            assertEquals(chunkLocalZ, chunkLocalLocationZ(chunkLocalLocation));
        }
    }

    @Test
    void testGetChunkBlock() {
        //////////////////////////////////////////////////////////////////////////////////////////////
        // Note: verify(..).foo() requires some time so full coverage of XYZ requires too much time //
        // because of this axises are stepped by random value                                       //
        //////////////////////////////////////////////////////////////////////////////////////////////

        int x = 0, y = 0, z = 0;
        while ((x += RandomUtils.nextInt(1)) < 16
                && (z += RandomUtils.nextInt(1)) < 16
                && (y += RandomUtils.nextInt(16)) < 255) {
            val chunk = mock(Chunk.class); // mocked here in case all 3 random increments are 0
            getChunkBlock(chunk, ChunkUtil.toChunkLocalLocationShort(x, y, z));
            verify(chunk).getBlock(x, y, z);
        }
    }
}