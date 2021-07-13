package ru.progrm_jarvis.minecraft.commons.util.shutdown;

/**
 * An object which may be shutdown and should be once its usage is over.
 */
@FunctionalInterface
public interface Shutdownable extends AutoCloseable {

    /**
     * Shuts this object down.
     *
     * @apiNote this means that the object may be unusable in future
     */
    void shutdown();

    @Override
    default void close() {
        shutdown();
    }
}
