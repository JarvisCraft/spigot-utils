package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractConcurrentCallbackTimer extends AbstractSchedulerRunnable {

    @NonNull AtomicLong counter;

    @Override
    public void run() {
        final long value;

        if ((value = counter.decrementAndGet()) == 0) {
            cancelTask();
            onOver();
        }
        onTick(value);
    }

    protected final void cancelTask() {
        super.cancel();
    }

    @Override
    public void cancel() {
        cancelTask();
        onAbort();
    }

    protected void onTick(long counter) {}

    protected void onAbort() {}

    protected void onOver() {}

    public static SchedulerRunnable create(final @NonNull LongConsumer task, final long counter) {
        return new SimpleConcurrentCallbackTimer(task, new AtomicLong(counter));
    }

    public static SchedulerRunnable create(final @NonNull LongConsumer task, final @NonNull AtomicLong counter) {
        return new SimpleConcurrentCallbackTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class SimpleConcurrentCallbackTimer extends AbstractConcurrentCallbackTimer {

        @NotNull LongConsumer task;

        public SimpleConcurrentCallbackTimer(final @NotNull LongConsumer task,
                                             final @NotNull AtomicLong counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void onTick(final long counter) {
            task.accept(counter);
        }
    }
}
