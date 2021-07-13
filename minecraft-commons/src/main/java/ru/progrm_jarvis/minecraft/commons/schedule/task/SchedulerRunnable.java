package ru.progrm_jarvis.minecraft.commons.schedule.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * An abstraction duplicating behaviour of {@link org.bukkit.scheduler.BukkitRunnable}
 * except for {@link org.bukkit.scheduler.BukkitRunnable#getTaskId()} method.
 */
public interface SchedulerRunnable extends Runnable {

    boolean isCancelled();

    void cancel();

    BukkitTask runTask(@NotNull Plugin plugin);

    BukkitTask runTaskAsynchronously(@NotNull Plugin plugin);

    BukkitTask runTaskLater(@NotNull Plugin plugin, long delay);

    BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay);

    BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period);

    BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period);
}
