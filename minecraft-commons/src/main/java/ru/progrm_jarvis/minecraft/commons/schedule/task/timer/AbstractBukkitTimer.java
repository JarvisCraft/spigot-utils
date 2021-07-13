package ru.progrm_jarvis.minecraft.commons.schedule.task.timer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run for a specified amount of times after which it is cancelled.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBukkitTimer extends AbstractSchedulerRunnable {

    long counter;

    @Override
    public void run() {
        if (--counter == 0) {
            cancelTask();
            onOver();
        }
        onTick();
    }

    protected final void cancelTask() {
        super.cancel();
    }

    @Override
    public void cancel() {
        cancelTask();
        onAbort();
    }

    protected void onTick() {}

    protected void onAbort() {}

    protected void onOver() {}

    public static SchedulerRunnable create(final @NonNull Runnable task, final long counter) {
        return new SimpleBukkitTimer(task, counter);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class SimpleBukkitTimer extends AbstractBukkitTimer {

        @NotNull Runnable task;

        public SimpleBukkitTimer(final @NotNull Runnable task,
                                 final long counter) {
            super(counter);
            this.task = task;
        }

        @Override
        protected void onTick() {
            task.run();
        }
    }
}
