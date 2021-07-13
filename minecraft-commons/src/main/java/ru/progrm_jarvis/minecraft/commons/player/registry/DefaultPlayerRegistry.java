package ru.progrm_jarvis.minecraft.commons.player.registry;

import com.google.common.base.Preconditions;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class DefaultPlayerRegistry implements PlayerRegistry {

    @NonNull Plugin plugin;
    @Getter @NonNull Set<Player> players;
    @Getter boolean global;
    @NonNull Set<PlayerContainer> playerContainers = Collections.newSetFromMap(new WeakHashMap<>());
    Lock playerContainersReadLock;
    Lock playerContainersWriteLock;

    Listener listener;

    public DefaultPlayerRegistry(final @NonNull Plugin plugin, final @NonNull Set<Player> playerSet,
                                 final boolean global) {
        Preconditions.checkArgument(playerSet.isEmpty(), "playerSet should be empty");

        this.plugin = plugin;
        players = playerSet;
        this.global = global;

        {
            ReadWriteLock playerContainersLock = new ReentrantReadWriteLock();
            playerContainersReadLock = playerContainersLock.readLock();
            playerContainersWriteLock = playerContainersLock.writeLock();
        }

        plugin.getServer().getPluginManager().registerEvents(listener = global ? new Listener() {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            public final void onPlayerJoin(final PlayerJoinEvent event) {
                addPlayer(event.getPlayer(), false);
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            public final void onPlayerQuit(final PlayerQuitEvent event) {
                removePlayer(event.getPlayer());
            }
        } : new Listener() {

            @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
            public final void onPlayerQuit(final PlayerQuitEvent event) {
                removePlayer(event.getPlayer());
            }
        }, plugin);
    }

    public DefaultPlayerRegistry(final @NonNull Plugin plugin, final boolean concurrent, final boolean global) {
        this(plugin, concurrent ? new HashSet<>() : ConcurrentHashMap.newKeySet(), global);
    }

    public DefaultPlayerRegistry(final @NonNull Plugin plugin) {
        this(plugin, true, true);
    }

    public void addPlayer(final Player player, final boolean force) {
        players.add(player);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            playerContainersReadLock.lock();
            try {
                for (val playerContainer : playerContainers) if (force || playerContainer.isGlobal()) playerContainer
                        .addPlayer(player);
            } finally {
                playerContainersReadLock.unlock();
            }
        });
    }

    @Override
    public void addPlayer(final Player player) {
        addPlayer(player, true);
    }

    @Override
    public void removePlayer(final Player player) {
        players.remove(player);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            playerContainersReadLock.lock();
            try {
                for (val playerContainer : playerContainers) playerContainer.removePlayer(player);
            } finally {
                playerContainersReadLock.unlock();
            }
        });
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return players.contains(player);
    }

    @Override
    public <C extends PlayerContainer> C register(final C playerContainer) {
        playerContainersWriteLock.lock();
        try {
            playerContainers.add(playerContainer);
        } finally {
            playerContainersWriteLock.unlock();
        }

        return playerContainer;
    }

    @Override
    public <C extends PlayerContainer> C unregister(final C playerContainer) {
        playerContainersWriteLock.lock();
        try {
            playerContainers.remove(playerContainer);
        } finally {
            playerContainersWriteLock.unlock();
        }

        return playerContainer;
    }

    @Override
    public void shutdown() {
        HandlerList.unregisterAll(listener);
    }
}
