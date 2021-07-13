package ru.progrm_jarvis.minecraft.commons.player.registry;

import lombok.NonNull;
import lombok.val;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainers;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface PlayerRegistry extends PlayerContainer, Shutdownable {

    <C extends PlayerContainer> C register(C playerContainer);

    <C extends PlayerContainer> C unregister(C playerContainer);

    default PlayerContainer register(final @NonNull Collection<Player> playerCollection, final boolean global) {
        val playerContainer = PlayerContainers.wrap(playerCollection, global);
        register(playerContainer);

        return playerContainer;
    }

    default <T> PlayerContainer register(final @NonNull Map<Player, T> playerCollection,
                                         final @NonNull Function<Player, T> defaultValueSupplier,
                                         final boolean global) {
        val playerContainer = PlayerContainers.wrap(playerCollection, defaultValueSupplier, global);
        register(playerContainer);

        return playerContainer;
    }
}
