package ru.progrm_jarvis.minecraft.commons.schedule.task;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * A {@link ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable}
 * which is run until its hook returns {@code false} on tick.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CancellingBukkitRunnable extends AbstractSchedulerRunnable {

    @Override
    public void run() {
        if (tick()) cancel();
    }

    protected abstract boolean tick();

    public static SchedulerRunnable create(@NonNull BooleanSupplier task) {
        return new FunctionalCancellingBukkitRunnable(task);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE) // `RequiredArgsConstructor` is not used as field is `@NotNull`
    private static final class FunctionalCancellingBukkitRunnable extends CancellingBukkitRunnable {

        @NotNull BooleanSupplier task;

        @Override
        protected boolean tick() {
            return task.getAsBoolean();
        }
    }
}
