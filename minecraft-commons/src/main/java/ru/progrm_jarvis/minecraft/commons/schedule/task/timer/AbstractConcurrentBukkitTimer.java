package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractConcurrentBukkitTimer extends AbstractSchedulerRunnable {

    @NonNull AtomicLong counter;

    @Override
    public void run() {
        if (counter.decrementAndGet() == 0) {
            cancelTask();
            onOver();
        }
        tick();
    }

    protected final void cancelTask() {
        super.cancel();
    }

    @Override
    public void cancel() {
        cancelTask();
        onAbort();
    }

    protected void tick() {}

    protected void onAbort() {}

    protected void onOver() {}

    public static SchedulerRunnable create(final @NonNull Runnable task, final long counter) {
        return new SimpleConcurrentBukkitTimer(task, new AtomicLong(counter));
    }

    public static SchedulerRunnable create(final @NonNull Runnable task, final @NonNull AtomicLong counter) {
        return new SimpleConcurrentBukkitTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class SimpleConcurrentBukkitTimer extends AbstractConcurrentBukkitTimer {

        @NotNull Runnable task;

        public SimpleConcurrentBukkitTimer(final @NotNull Runnable task,
                                           final @NotNull AtomicLong counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void tick() {
            task.run();
        }
    }
}
