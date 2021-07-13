package ru.progrm_jarvis.minecraft.commons.player.registry;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainers;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@UtilityClass
public class PlayerRegistries {

    private final Map<Plugin, PlayerRegistry> DEFAULT_REGISTRIES = new ConcurrentHashMap<>();

    public PlayerRegistry defaultRegistry(final @NonNull Plugin plugin) {
        return DEFAULT_REGISTRIES.computeIfAbsent(plugin, DefaultPlayerRegistry::new);
    }

    public <C extends PlayerContainer> C registerInDefaultRegistry(final @NonNull Plugin plugin,
                                                                   final @NonNull C playerContainer) {
        return defaultRegistry(plugin).register(playerContainer);
    }

    public PlayerContainer registerInDefaultRegistry(final @NonNull Plugin plugin,
                                                     final @NonNull Collection<Player> collectionOfPlayers,
                                                     final boolean global) {
        return registerInDefaultRegistry(plugin, PlayerContainers.wrap(collectionOfPlayers, global));
    }

    public <T> PlayerContainer registerInDefaultRegistry(final @NonNull Plugin plugin,
                                                         final @NonNull Map<Player, T> mapOfPlayers,
                                                         final @NonNull Function<Player, T> defaultValueSupplier,
                                                         final boolean global) {
        return registerInDefaultRegistry(plugin, PlayerContainers.wrap(mapOfPlayers, defaultValueSupplier, global));
    }

    public <T> PlayerContainer registerInDefaultRegistry(final @NonNull Plugin plugin,
                                                         final @NonNull Map<Player, T> mapOfPlayers) {
        return registerInDefaultRegistry(plugin, PlayerContainers.wrap(mapOfPlayers));
    }
}
