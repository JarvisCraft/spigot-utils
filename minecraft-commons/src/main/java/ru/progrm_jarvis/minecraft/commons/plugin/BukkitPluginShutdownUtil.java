package ru.progrm_jarvis.minecraft.commons.plugin;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.javacommons.collection.concurrent.ConcurrentCollections;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility responsible for handling plugin shutdown hooks.
 */
@UtilityClass
public class BukkitPluginShutdownUtil {

    private final Map<Plugin, PluginShutdownHandler> PLUGIN_SHUTDOWN_HANDLERS = new ConcurrentHashMap<>();

    private PluginShutdownHandler getOrCreateShutdownHandler(final Plugin plugin) {
        return PLUGIN_SHUTDOWN_HANDLERS.computeIfAbsent(plugin, PluginShutdownHandler::new);
    }

    private Optional<PluginShutdownHandler> getOptionalShutdownHandler(final Plugin plugin) {
        return Optional.ofNullable(PLUGIN_SHUTDOWN_HANDLERS.get(plugin));
    }

    public void addShutdownHook(final Plugin plugin, final Shutdownable callback) {
        getOrCreateShutdownHandler(plugin).shutdownHooks.add(callback);
    }

    public void addShutdownHooks(final Plugin plugin, final Shutdownable... callbacks) {
        getOrCreateShutdownHandler(plugin).shutdownHooks.addAll(Arrays.asList(callbacks));
    }

    public void addShutdownHooks(final Plugin plugin, final Collection<Shutdownable> callbacks) {
        getOrCreateShutdownHandler(plugin).shutdownHooks.addAll(callbacks);
    }

    public void removeShutdownHook(final Plugin plugin, final Shutdownable callback) {
        getOptionalShutdownHandler(plugin).ifPresent(handler -> handler.shutdownHooks.remove(callback));
    }

    public void removeShutdownHooks(final Plugin plugin, final Shutdownable... callbacks) {
        getOptionalShutdownHandler(plugin).ifPresent(handler -> handler.shutdownHooks.removeAll(Arrays.asList(callbacks)));
    }

    public void removeShutdownHooks(final Plugin plugin, final Collection<Shutdownable> callback) {
        getOptionalShutdownHandler(plugin).ifPresent(handler -> handler.shutdownHooks.removeAll(callback));
    }

    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class PluginShutdownHandler implements Listener {

        @NonNull Plugin plugin;
        @NonNull List<Shutdownable> shutdownHooks = ConcurrentCollections.concurrentList(new ArrayList<>());

        private PluginShutdownHandler(final @NonNull Plugin plugin) {
            this.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler(ignoreCancelled = true)
        public void onPluginDisable(final PluginDisableEvent event) {
            // check if the plugin disabled is the right one
            if (event.getPlugin() == plugin) {
                val thisShutdownHooks = shutdownHooks;
                while (!thisShutdownHooks.isEmpty()) {
                    val hook = thisShutdownHooks.remove(0);
                    hook.shutdown();
                }
            }
        }
    }
}
