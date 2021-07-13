package ru.progrm_jarvis.minecraft.commons.schedule.task;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractSchedulerRunnable implements SchedulerRunnable {

    @NonFinal volatile BukkitTask task = null;
    @SuppressWarnings("ZeroLengthArrayAllocation") Object taskAccessLock = new Object[0];

    @Override
    @Synchronized("taskAccessLock")
    public boolean isCancelled() {
        final BukkitTask task;
        if ((task = this.task) == null) throw new IllegalStateException("This task has not yet started");

        return task.isCancelled();
    }

    @Override
    @Synchronized("taskAccessLock")
    public void cancel() {
        final BukkitTask task;
        if ((task = this.task) == null) throw new IllegalStateException("This task has not yet started");

        task.cancel();
    }

    @Override
    @Synchronized("taskAccessLock")
    public BukkitTask runTask(final @NotNull Plugin plugin) {
        if (task != null) throw new IllegalStateException("This task has already started");

        return task = plugin.getServer().getScheduler().runTask(plugin, this);
    }

    @Override
    @Synchronized("taskAccessLock")
    public BukkitTask runTaskAsynchronously(final @NotNull Plugin plugin) {
        if (task != null) throw new IllegalStateException("This task has already started");

        return task = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this);
    }

    @Override
    @Synchronized("taskAccessLock")
    public BukkitTask runTaskLater(final @NotNull Plugin plugin, final long delay) {
        if (task != null) throw new IllegalStateException("This task has already started");

        return task = plugin.getServer().getScheduler().runTaskLater(plugin, this, delay);
    }

    @Override
    @Synchronized("taskAccessLock")
    public BukkitTask runTaskLaterAsynchronously(final @NotNull Plugin plugin, final long delay) {
        if (task != null) throw new IllegalStateException("This task has already started");

        return task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, this, delay);
    }

    @Override
    @Synchronized("taskAccessLock")
    public BukkitTask runTaskTimer(final @NotNull Plugin plugin, final long delay, final long period) {
        if (task != null) throw new IllegalStateException("This task has already started");

        return task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, delay, period);
    }

    @Override
    @Synchronized("taskAccessLock")
    public BukkitTask runTaskTimerAsynchronously(final @NotNull Plugin plugin, final long delay, final long period) {
        if (task != null) throw new IllegalStateException("This task has already started");

        return task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
    }
}
