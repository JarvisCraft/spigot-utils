package ru.progrm_jarvis.minecraft.commons.async;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Async runner based on {@link net.md_5.bungee.api.scheduler.TaskScheduler}.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BungeeSchedulerAsyncRunner implements AsyncRunner {

    /**
     * Plugin to be used for scheduling asynchronous operations.
     */
    @NonNull Plugin plugin;

    @Override
    public void runAsynchronously(final Runnable operation) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, operation);
    }
}
