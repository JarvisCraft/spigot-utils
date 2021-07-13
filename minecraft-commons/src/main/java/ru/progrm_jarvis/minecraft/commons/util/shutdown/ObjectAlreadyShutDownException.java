package ru.progrm_jarvis.minecraft.commons.util.shutdown;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * An exception which may be thrown whenever {@link Shutdownable#shutdown()}
 * is called multiple times on an object which becomes unavailable after the first call.
 */
public class ObjectAlreadyShutDownException extends RuntimeException {

    /**
     * Instantiates a new exception.
     *
     * @param message message describing the reason of the exception
     */
    public ObjectAlreadyShutDownException(final @NonNull String message) {
        super(message);
    }

    /**
     * Instantiates a new exception with default message.
     */
    public ObjectAlreadyShutDownException() {
        this("Object is already shut down");
    }

    /**
     * Instantiates a new exception.
     *
     * @param object object whose {@link Shutdownable#shutdown()} was called multiple times
     * although it was expected to be called only once
     */
    public ObjectAlreadyShutDownException(final @Nullable Shutdownable object) {
        this(object == null ? "Object is already shut down" : object + " is already shut down");
    }
}
