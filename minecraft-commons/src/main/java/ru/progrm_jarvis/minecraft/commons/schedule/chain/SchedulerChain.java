package ru.progrm_jarvis.minecraft.commons.schedule.chain;

import lombok.NonNull;

/**
 * A chain of tasks called in the specified order.
 */
public interface SchedulerChain extends Runnable {

    void interrupt();

    interface Builder {

        SchedulerChain build();

        Builder delay(final long delay);

        Builder then(@NonNull Runnable task);

        Builder then(@NonNull Runnable task, final long delay);

        Builder thenRepeat(@NonNull Runnable task, final long times);

        Builder thenRepeat(@NonNull Runnable task, final long times, final long delay);
    }

    final class EmptyStub implements SchedulerChain {

        public static final EmptyStub INSTANCE = new EmptyStub();

        @Override
        public void interrupt() {}

        @Override
        public void run() {}
    }
}
