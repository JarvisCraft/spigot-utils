package ru.progrm_jarvis.minecraft.commons.schedule.pool;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface LoopPool {

    @NotNull ShutdownHook addTask(@NotNull Runnable task, long period, boolean async);

    @FunctionalInterface
    interface ShutdownHook extends AutoCloseable {

        @Override
        void close();
    }
}
