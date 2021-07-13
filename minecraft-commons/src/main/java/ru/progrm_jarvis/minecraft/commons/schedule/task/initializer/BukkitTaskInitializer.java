package ru.progrm_jarvis.minecraft.commons.schedule.task.initializer;

import org.bukkit.scheduler.BukkitTask;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;

@FunctionalInterface
public interface BukkitTaskInitializer extends Shutdownable {

    BukkitTask initialize();

    @Override
    default void shutdown() {}
}
