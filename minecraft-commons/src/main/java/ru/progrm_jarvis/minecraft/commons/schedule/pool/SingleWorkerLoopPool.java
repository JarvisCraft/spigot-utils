package ru.progrm_jarvis.minecraft.commons.schedule.pool;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public final class SingleWorkerLoopPool implements LoopPool {

    @NonNull Plugin plugin;
    @NonNull BukkitScheduler scheduler;
    @NonNull Long2ObjectMap<TaskRunner> syncTasks, asyncTasks;

    @Override
    public @NotNull ShutdownHook addTask(final @NotNull Runnable task, final long period, final boolean async) {
        return (async ? asyncTasks.computeIfAbsent(period, p -> {
            val runner = new PaperTaskRunner(ConcurrentHashMap.newKeySet());
            runner.setup(
                    scheduler.runTaskTimer(plugin, runner, period, period), () -> asyncTasks.remove(period)
            );

            return runner;
        }) : syncTasks.computeIfAbsent(period, p -> {
            val runner = new PaperTaskRunner(ConcurrentHashMap.newKeySet());
            runner.setup(
                    scheduler.runTaskTimer(plugin, runner, period, period), () -> syncTasks.remove(period)
            );
            return runner;
        })).addTask(task);
    }

    public static LoopPool create(final @NonNull Plugin plugin) {
        return new SingleWorkerLoopPool(
                plugin, plugin.getServer().getScheduler(),
                new Long2ObjectOpenHashMap<>(4), new Long2ObjectOpenHashMap<>(4)
        );
    }

    interface TaskRunner extends AutoCloseable {

        @NotNull ShutdownHook addTask(@NotNull Runnable task);

        @Override
        void close();
    }

    interface BukkitTaskRunner extends TaskRunner, Runnable {

        void setup(@NotNull BukkitTask task, @NotNull Runnable disabler);
    }

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    private static class PaperTaskRunner implements BukkitTaskRunner {

        @NotNull Collection<Runnable> tasks;
        @NonFinal /* because class initialization order */ @Nullable BukkitTask owningTask;
        @NonFinal /* because class initialization order */ @Nullable Runnable disabler;

        public void run() {
            for (val task : tasks) task.run();
        }

        @Override
        public void setup(final @NotNull BukkitTask task, final @NotNull Runnable disabler) {
            owningTask = task;
            this.disabler = disabler;
        }

        @Override
        public @NotNull ShutdownHook addTask(final @NotNull Runnable task) {
            tasks.add(task);

            return () -> removeTask(task);
        }

        private void removeTask(final @NotNull Runnable task) {
            tasks.remove(task);
            if (tasks.isEmpty() && owningTask != null) {
                assert disabler != null;
                disabler.run();

                close();
            }
        }

        @Override
        public void close() {
            assert owningTask != null;
            owningTask.cancel();
        }
    }
}
