package ru.progrm_jarvis.minecraft.commons.schedule.task.conditional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

import java.util.function.BooleanSupplier;

/**
 * A task which will run until the specified condition becomes false.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractConditionalTask extends AbstractSchedulerRunnable {

    protected abstract boolean tryRun();

    @Override
    public void run() {
        if (!tryRun()) cancel();
    }

    public static SchedulerRunnable create(final @NonNull BooleanSupplier conditionalTask) {
        return new SimpleConditionalTask(conditionalTask);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class SimpleConditionalTask extends AbstractConditionalTask {

        @NonNull BooleanSupplier conditionalTask;

        @Override
        protected boolean tryRun() {
            return conditionalTask.getAsBoolean();
        }
    }
}
