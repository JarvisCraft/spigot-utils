package ru.progrm_jarvis.minecraft.commons.chunk;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.bukkit.Chunk;

/**
 * Immutable value storing chunk and its location
 */
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class ChunkLocalLocation {

    /**
     * Chunk containing the location
     */
    Chunk chunk;

    /**
     * Chunk-local location in a {@code short} representation
     */
    short location;
}
