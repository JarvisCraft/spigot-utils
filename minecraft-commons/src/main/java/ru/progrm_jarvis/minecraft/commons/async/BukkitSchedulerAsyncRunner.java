package ru.progrm_jarvis.minecraft.commons.async;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;

/**
 * Async runner based on {@link org.bukkit.scheduler.BukkitScheduler}.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BukkitSchedulerAsyncRunner implements AsyncRunner {

    /**
     * Plugin to be used for scheduling asynchronous operations.
     */
    @NonNull Plugin plugin;

    @Override
    public void runAsynchronously(final Runnable operation) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, operation);
    }
}
