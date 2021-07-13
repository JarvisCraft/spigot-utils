package ru.progrm_jarvis.minecraft.commons.schedule.chain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BukkitSchedulerChain extends AbstractSchedulerChain {

    @NonNull AtomicBoolean running = new AtomicBoolean();

    Plugin plugin;
    @NonNull BukkitScheduler scheduler;
    boolean async;
    @NonNull Iterable<ChainedTask> tasks;

    final @NonNull Object currentTaskMutex;

    @NonFinal @NonNull volatile Iterator<ChainedTask> iterator;
    @NonFinal volatile BukkitTask currentTask;

    public static SchedulerChain.Builder builder(final Plugin plugin, final boolean async) {
        return new Builder(plugin, async, new ArrayDeque<>());
    }

    protected BukkitSchedulerChain(final @NonNull Plugin plugin, final boolean async,
                                   final @NonNull Iterable<ChainedTask> tasks) {
        this.plugin = plugin;
        scheduler = plugin.getServer().getScheduler();
        this.async = async;
        this.tasks = tasks;

        //noinspection ZeroLengthArrayAllocation: mutex object
        currentTaskMutex = new Object[0];
    }

    @Override
    public void run() {
        if (running.compareAndSet(false, true)) {
            iterator = tasks.iterator();

            tryRunNextTask();
        } else throw new IllegalStateException("This BukkitSchedulerChain has already been started");
    }

    @Override
    public void interrupt() {
        if (running.get()) reset();
    }

    protected void reset() {
        if (currentTask != null) {
            synchronized (currentTaskMutex) {
                if (currentTask != null) {
                    currentTask.cancel();
                    currentTask = null;
                }
            }
        }
        running.set(false);
    }

    protected void tryRunNextTask() {
        if (iterator.hasNext()) {
            val task = iterator.next();

            val nextRunnable = createNextRunnable(task, task.getRunTimes());
            val delay = task.getDelay();

            synchronized (currentTaskMutex) {
                currentTask = async
                        ? scheduler.runTaskLaterAsynchronously(plugin, nextRunnable, delay)
                        : scheduler.runTaskLater(plugin, nextRunnable, delay);
            }
        } else reset();
    }

    protected Runnable createNextRunnable(final @NonNull Runnable runnable, final long times) {
        return () -> {
            var mutableTimes = times;
            // call the required tasks needed amount of time
            System.out.println("Run times: " + times);
            while (mutableTimes-- > 0) runnable.run();

            // pick next task (if there is one) and execute it
            tryRunNextTask();
        };
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    protected static class Builder extends AbstractSchedulerChain.Builder {

        @NonNull Plugin plugin;
        boolean async;

        public Builder(final @NonNull Plugin plugin, final boolean async, final @NonNull Queue<ChainedTask> tasks) {
            super(tasks);
            this.plugin = plugin;
            this.async = async;
        }

        @Override
        public SchedulerChain build() {
            return tasks.isEmpty()
                    ? EmptyStub.INSTANCE
                    : new BukkitSchedulerChain(plugin, async, new ArrayList<>(tasks));
        }
    }
}
