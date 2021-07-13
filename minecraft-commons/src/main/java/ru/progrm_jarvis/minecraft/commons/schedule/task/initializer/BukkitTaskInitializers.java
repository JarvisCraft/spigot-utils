package ru.progrm_jarvis.minecraft.commons.schedule.task.initializer;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import ru.progrm_jarvis.minecraft.commons.schedule.task.SchedulerRunnable;

@UtilityClass
public class BukkitTaskInitializers {

    public BukkitTaskInitializer createTaskInitializer(final @NonNull Plugin plugin, final boolean async,
                                                       final @NonNull Runnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return async
                        ? plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable)
                        : plugin.getServer().getScheduler().runTask(plugin, runnable);
            }
        };
    }

    public BukkitTaskInitializer createTaskInitializer(final @NonNull Plugin plugin, final boolean async,
                                                       final @NonNull SchedulerRunnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return task = async ? runnable.runTaskAsynchronously(plugin) : runnable.runTask(plugin);
            }
        };
    }

    public BukkitTaskInitializer createTimerTaskInitializer(final @NonNull Plugin plugin, final boolean async,
                                                            final long delay, final long period,
                                                            final @NonNull Runnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return task = async
                        ? plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period)
                        : plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
            }
        };
    }

    public BukkitTaskInitializer createTimerTaskInitializer(final @NonNull Plugin plugin, final boolean async,
                                                            final long delay, final long period,
                                                            final @NonNull SchedulerRunnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return async
                        ? runnable.runTaskTimerAsynchronously(plugin, delay, period)
                        : runnable.runTaskTimer(plugin, delay, period);
            }
        };
    }

    public BukkitTaskInitializer createDelayedTaskInitializer(final @NonNull Plugin plugin, final boolean async,
                                                              final long delay,
                                                              final @NonNull Runnable runnable) {
        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return task = async
                        ? plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay)
                        : plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
            }
        };
    }

    public BukkitTaskInitializer createDelayedTaskInitializer(final @NonNull Plugin plugin, final boolean async,
                                                              final long delay,
                                                              final @NonNull SchedulerRunnable runnable) {

        return new AbstractBukkitTaskInitializer() {

            @Override
            public BukkitTask init() {
                return async
                        ? runnable.runTaskLaterAsynchronously(plugin, delay)
                        : runnable.runTaskLater(plugin, delay);
            }
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
    private abstract static class AbstractBukkitTaskInitializer implements BukkitTaskInitializer {

        @NonFinal BukkitTask task;

        protected abstract BukkitTask init();

        @Override
        public final BukkitTask initialize() {
            if (task == null || task.isCancelled()) synchronized (this) {
                if (task == null || task.isCancelled()) task = init();
            }

            return task;
        }

        @Override
        public final void shutdown() {
            if (!task.isCancelled()) synchronized (this) {
                if (!task.isCancelled()) task.cancel();
            }
        }
    }
}
