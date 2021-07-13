package ru.progrm_jarvis.minecraft.commons.player.collection;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@UtilityClass
public class PlayerContainers {

    public PlayerContainer wrap(final @NonNull Collection<Player> collectionOfPlayers,
                                final boolean global) {
        return new PlayerContainerCollectionWrapper(collectionOfPlayers, global);
    }

    public <T> PlayerContainer wrap(final @NonNull Map<Player, T> mapOfPlayers,
                                    final @NonNull Function<Player, T> defaultValueSupplier,
                                    final boolean global) {
        if (global) {
            val container = new PlayerContainerMapWrapper<>(mapOfPlayers, defaultValueSupplier, true);
            container.addOnlinePlayers();

            return container;
        }

        return new PlayerContainerMapWrapper<>(mapOfPlayers, defaultValueSupplier, false);
    }

    // non-global
    public <T> PlayerContainer wrap(final @NonNull Map<Player, T> mapOfPlayers) {
        return new PlayerContainerMapWrapper<>(mapOfPlayers, player -> {
            throw new UnsupportedOperationException("Players cannot be directly added to this non-global player-map");
        }, false);
    }

    @Value
    private static class PlayerContainerCollectionWrapper implements PlayerContainer {

        @NonNull Collection<Player> collection;
        boolean global;

        public PlayerContainerCollectionWrapper(final @NonNull Collection<Player> collection, final boolean global) {
            this.collection = collection;
            this.global = global;

            if (global) addOnlinePlayers();
        }

        @Override
        public void addPlayer(final Player player) {
            collection.add(player);
        }

        @Override
        public void removePlayer(final Player player) {
            collection.remove(player);
        }

        @Override
        public boolean containsPlayer(final Player player) {
            return collection.contains(player);
        }

        @Override
        public Collection<? extends Player> getPlayers() {
            return collection;
        }
    }

    @Value
    private static class PlayerContainerMapWrapper<T> implements PlayerContainer {

        @NonNull Map<Player, T> map;
        @NonNull Set<Player> playersView;
        @NonNull Function<Player, T> defaultValueSupplier;
        boolean global;

        public PlayerContainerMapWrapper(final @NonNull Map<Player, T> map,
                                               final @NonNull Function<Player, T> defaultValueSupplier,
                                               final boolean global) {
            this.map = map;
            playersView = Collections.unmodifiableSet(map.keySet());
            this.defaultValueSupplier = defaultValueSupplier;
            this.global = global;
        }

        @Override
        public void addPlayer(final Player player) {
            map.put(player, defaultValueSupplier.apply(player));
        }

        @Override
        public void removePlayer(final Player player) {
            map.remove(player);
        }

        @Override
        public boolean containsPlayer(final Player player) {
            return map.containsKey(player);
        }

        @Override
        public Collection<? extends Player> getPlayers() {
            return playersView;
        }
    }
}
