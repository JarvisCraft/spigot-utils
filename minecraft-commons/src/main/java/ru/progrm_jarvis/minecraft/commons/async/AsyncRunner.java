package ru.progrm_jarvis.minecraft.commons.async;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An object capable of performing asynchronous operations.
 */
@FunctionalInterface
public interface AsyncRunner {

    /**
     * Performs the specified operations asynchronously.
     *
     * @param operation operation to perform asynchronously
     */
    void runAsynchronously(final Runnable operation);

    /**
     * Performs the specified operations asynchronously.
     *
     * @param operation operation to perform asynchronously
     * @param callback callback to handle the resulting value of the operation
     * @param <T> type of value returned by the operation
     */
    default <T> void runAsynchronously(final Supplier<T> operation, final Consumer<T> callback) {
        runAsynchronously(() -> callback.accept(operation.get()));
    }
}
