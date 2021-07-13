package ru.progrm_jarvis.minecraft.commons.chunk;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Utilities related to chunks.
 * <p>
 * There are general conventions for type=methods:
 *
 * <dl>
 *     <dt>{@code Foo [bar]foo(Bar...)}</dt>
 *     <dd>return the specified <i>bar</i> value's <i>foo</i>component (other data is lost)</dd>
 *
 *     <dt>{@code Foo [bar]toFoo(Bar...)}</dt>
 *     <dd>convert the specified <i>bar</i> value to its <i>foo</i> representation (no data is lost)</dd>
 *
 *     <dt>{@code Foo <action>[Bar]Foo(Bar...)}</dt>
 *     <dd>performs the action specified on <i>bar</i> using <i>foo</i></dd>
 * </dl>
 *
 * @apiNote chunks are (by default) returned as a single {@code long} as the limit of chunk at non-Y-axis is 3750000
 * where 32 most significant bits stand for X-coordinate and 32 least significant bits stand for Z-coordinate
 * @apiNote chunks-local locations are (by default) returned as a single {@code short}
 * whose bits are ordered as 4 bits for X-coordinate, 8 bits for Y-coordinate, 4 bits for Z-coordinate
 * @see <a href="https://minecraft.gamepedia.com/Chunk">General info about chinks</a>
 */
@UtilityClass
public class ChunkUtil {

    ///////////////////////////////////////////////////////////////////////////
    // Range checks
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Performs a range-check of chunk-local coordinates.
     *
     * @param x chunk-local X-coordinate which should normally be between 0 and 15
     * @param y chunk-local Y-coordinate which should normally be between 0 and 255
     * @param z chunk-local Z-coordinate which should normally be between 0 and 15
     * @throws IllegalArgumentException if any of coordinates is out of allowed range
     */
    public void rangeCheckChunkLocal(final int x, final int y, final int z) {
        rangeCheckChunkLocalX(x);
        rangeCheckChunkLocalY(y);
        rangeCheckChunkLocalX(z);
    }

    /**
     * Performs a range-check of chunk-local X-coordinate.
     *
     * @param x chunk-local X-coordinate which should normally be between 0 and 15
     * @throws IllegalArgumentException if {@code x} is not in range between 0 and 15
     */
    public void rangeCheckChunkLocalX(final int x) {
        checkArgument(x >= 0 && x <= 15, "x should be between 0 and 15");
    }

    /**
     * Performs a range-check of chunk-local Y-coordinate.
     *
     * @param y chunk-local Y-coordinate which should normally be between 0 and 255
     * @throws IllegalArgumentException if {@code y} is not in range between 0 and 255
     */
    public void rangeCheckChunkLocalY(final int y) {
        checkArgument(y >= 0 && y <= 255, "z should be between 0 and 255");
    }

    /**
     * Performs a range-check of chunk-local Z-coordinate.
     *
     * @param z chunk-local Z-coordinate which should normally be between 0 and 15
     * @throws IllegalArgumentException if {@code z} is not in range between 0 and 15
     */
    public void rangeCheckChunkLocalZ(final int z) {
        checkArgument(z >= 0 && z <= 15, "z should be between 0 and 15");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Chunk
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns a single {@code long} value storing chunk data for X- and Z-axises.
     *
     * @param x X coordinate of a chunk
     * @param z Z coordinate of a chunk
     * @return chunk treated as {@code long}
     */
    public long toChunkLong(final int x, final int z) {
        return ((long) x << 32) | ((long) z & 0xFFFFFFFFL);
    }

    /**
     * Returns the X coordinate value from a long-serialized chunk
     *
     * @param longChunk chunk data treated as {@code long}
     * @return X coordinate of a chunk
     */
    public int chunkX(final long longChunk) {
        return (int) (longChunk >> 32);
    }

    /**
     * Returns the Z coordinate value from a long-serialized chunk
     *
     * @param longChunk chunk data treated as long
     * @return Z coordinate of a chunk
     */
    public int chunkZ(final long longChunk) {
        return (int) (longChunk);
    }

    /**
     * Gets the chunk by location in a world.
     *
     * @param x X coordinate of the location
     * @param z Y coordinate of the location
     * @return chunk location treated as {@code long}
     */
    public long chunkAt(final long x, final long z) {
        return toChunkLong((int) (x >> 4), (int) (z >> 4));
    }

    /**
     * Gets the chunk in the world specified from a {@code long} chunk representation.
     *
     * @param world world to get chunk from
     * @param chunk chunk treated as {@code long}
     * @return specified chunk of the world
     */
    public Chunk getChunk(final @NonNull World world, final long chunk) {
        return world.getChunkAt(chunkX(chunk), chunkZ(chunk));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Chunk local location
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Converts the specified x, y and z chunk-local coordinates to a {@code short}-representation
     *
     * @param chunkLocalX X-coordinate inside the chunk between 0 and 15
     * @param chunkLocalY Y-coordinate inside the chunk between 0 and 255
     * @param chunkLocalZ Z-coordinate inside the chunk between 0 and 15
     * @return {@code short}-representation of specified chunk-local coordinated
     *
     * @throws IllegalArgumentException if {@code x} is not in range [0; 15]
     * @throws IllegalArgumentException if {@code z} is not in range [0; 15]
     * @throws IllegalArgumentException if {@code y} is not in range [0; 255]
     */
    public short toChunkLocalLocationShort(final int chunkLocalX, final int chunkLocalY, final int chunkLocalZ) {
        rangeCheckChunkLocal(chunkLocalX, chunkLocalY, chunkLocalZ);

        return (short) (((chunkLocalX & 0xF) << 12) | ((chunkLocalY & 0xFF) << 4) | (chunkLocalZ & 0xF));
    }

    /**
     * Gets the X-coordinate from a {@code short}-representation of a chunk-local location.
     *
     * @param location {@code short}-representation of a chunk-local location
     * @return X-coordinate of a chunk local location
     */
    public int chunkLocalLocationX(final short location) {
        return (location >> 12) & 0xF;
    }

    /**
     * Gets the Y-coordinate from a {@code short}-representation of a chunk-local location.
     *
     * @param location {@code short}-representation of a chunk-local location
     * @return Y-coordinate of a chunk local location
     */
    public int chunkLocalLocationY(final short location) {
        return (location >> 4) & 0xFF;
    }

    /**
     * Gets the Z-coordinate from a {@code short}-representation of a chunk-local location.
     *
     * @param location {@code short}-representation of a chunk-local location
     * @return Z-coordinate of a chunk local location
     */
    public int chunkLocalLocationZ(final short location) {
        return location & 0xF;
    }

    /**
     * Gets the chunk-local X-coordinate value of the location.
     *
     * @param x X-coordinate
     * @return chunk-local X-coordinate
     */
    public int chunkLocalX(final int x) {
        return x & 0xF;
    }

    /**
     * Gets the chunk-local Y-coordinate value of the location.
     *
     * @param y Y-coordinate
     * @return chunk-local Y-coordinate
     */
    public int chunkLocalY(final int y) {
        return y & 0xFF;
    }

    /**
     * Gets the chunk-local Z-coordinate value of the location.
     *
     * @param z Z-coordinate
     * @return chunk-local Z-coordinate
     */
    public int chunkLocalZ(final int z) {
        return z & 0xF;
    }

    /**
     * Gets the specified location's chunk-local location.
     *
     * @param x X-coordinate of the location whose chunk-local location should be got
     * @param y Y-coordinate of the location whose chunk-local location should be got
     * @param z Z-coordinate of the location whose chunk-local location should be got
     * @return {@code short}-representation of location's chunk-local location
     *
     * @see #chunkLocalLocation(Location) is an allias for {@link Location} argument
     */
    public short chunkLocalLocation(final int x, final int y, final int z) {
        return toChunkLocalLocationShort(chunkLocalX(x), chunkLocalY(y), chunkLocalZ(z));
    }

    /**
     * Gets the specified location's chunk-local location.
     *
     * @param location location whose chunk-local location should be got
     * @return {@code short}-representation of location's chunk-local location
     *
     * @see #toChunkLocalLocationShort(int, int, int) is called with location's coordinates
     */
    public short chunkLocalLocation(final @NonNull Location location) {
        return chunkLocalLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Gets the block in a chunk from its chunk-local location in {@code short}-representation
     *
     * @param chunk chunk whose block to get
     * @param location {@code short}-representation of a chunk-local location
     * @return block from the chunk of specified chunk-local location
     */
    public Block getChunkBlock(final Chunk chunk, final short location) {
        return chunk.getBlock(
                chunkLocalLocationX(location), chunkLocalLocationY(location), chunkLocalLocationZ(location)
        );
    }

    /**
     * Converts location to it's chunk-local location representation.
     *
     * @param location location whose chunk-local location should be got
     * @return chunk local location of the location
     */
    public ChunkLocalLocation toChunkLocalLocation(final Location location) {
        return new ChunkLocalLocation(location.getChunk(), chunkLocalLocation(location));
    }
}
