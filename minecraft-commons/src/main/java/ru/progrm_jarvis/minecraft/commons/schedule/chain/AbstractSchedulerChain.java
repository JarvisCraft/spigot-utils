package ru.progrm_jarvis.minecraft.commons.schedule.chain;

import lombok.*;
import lombok.experimental.Delegate;

import javax.annotation.Nonnegative;
import java.util.Queue;

public abstract class AbstractSchedulerChain implements SchedulerChain {

    @ToString
    @RequiredArgsConstructor
    protected abstract static class Builder implements SchedulerChain.Builder {

        final @NonNull Queue<ChainedTask> tasks;

        @Override
        public SchedulerChain.Builder delay(final long delay) {
            tasks.add(new PauseTask(delay));

            return this;
        }

        @Override
        public SchedulerChain.Builder then(final @NonNull Runnable task) {
            tasks.add(new UndelayedTask(task));

            return this;
        }

        @Override
        public SchedulerChain.Builder then(final @NonNull Runnable task, final long delay) {
            tasks.add(new DelayedTask(task, delay));

            return this;
        }

        @Override
        public SchedulerChain.Builder thenRepeat(final @NonNull Runnable task, final long times) {
            tasks.add(new UndelayedRecalledTask(task, times));

            return this;
        }

        @Override
        public SchedulerChain.Builder thenRepeat(final @NonNull Runnable task, final long times, final long delay) {
            tasks.add(new DelayedRecalledTask(task, delay, times));

            return this;
        }
    }

    /**
     * An entry of the chain.
     */
    protected interface ChainedTask extends Runnable {

        /**
         * Gets the delay after which this element entry should be called.
         *
         * @return delay of this chain entry
         */
        long getDelay();

        /**
         * Gets the amount of times this task should be called.
         *
         * @return the amount of times this task should be called
         *
         * @apiNote the delay happens <b>once</b> after which the task is called multiple times without it
         */
        @Nonnegative default long getRunTimes() {
            return 1;
        }
    }

    @Value
    protected static class PauseTask implements ChainedTask {

        long delay;

        @Override
        public void run() {} // will even never be called

        @Override
        public long getRunTimes() {
            return 0;
        }
    }

    @Value
    protected static class UndelayedTask implements ChainedTask {

        @Delegate @NonNull Runnable runnable;

        @Override
        public long getDelay() {
            return 0;
        }
    }

    @Value
    protected static class UndelayedRecalledTask implements ChainedTask {

        @Delegate @NonNull Runnable runnable;
        long runTimes;

        @Override
        public long getDelay() {
            return 0;
        }
    }

    @Value
    protected static class DelayedTask implements ChainedTask {

        @Delegate @NonNull Runnable runnable;
        long delay;
    }

    @Value
    protected static class DelayedRecalledTask implements ChainedTask {

        @Delegate @NonNull Runnable runnable;
        long delay, runTimes;
    }
}
