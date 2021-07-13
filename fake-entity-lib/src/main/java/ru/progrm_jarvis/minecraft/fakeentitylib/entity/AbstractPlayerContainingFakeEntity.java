package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistryRegistration;

import java.util.*;

/**
 * Base for most common implementations of {@link ObservableFakeEntity} containing player logic base.
 */
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@PlayerRegistryRegistration(PlayerRegistryRegistration.Policy.MANUAL)
public abstract class AbstractPlayerContainingFakeEntity extends AbstractObservableFakeEntity {

    @NonNull Map<Player, Boolean> players;
    @NonNull Set<Player> playersView;

    public AbstractPlayerContainingFakeEntity(final int viewDistance, final boolean global,
                                              final @NonNull Location location,
                                              final @NonNull Map<Player, Boolean> players) {
        super(global, viewDistance, location);

        if (!players.isEmpty()) players.clear();

        this.players = players;
        playersView = Collections.unmodifiableSet(players.keySet());
    }

    @Override
    public Collection<? extends Player> getPlayers() {
        return playersView;
    }

    @Override
    public boolean isRendered(final @NonNull Player player) {
        return players.getOrDefault(player, false);
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return players.containsKey(player);
    }

    @Override
    public void addPlayer(final Player player) {
        final Map<Player, Boolean> thisPlayers;
        if (!(thisPlayers = players).containsKey(player)) if (shouldSee(player)) render(player);
        else thisPlayers.put(player, false);
    }

    @Override
    public void removePlayer(final Player player) {
        final Map<Player, Boolean> thisPlayers;
        final Boolean canSee;
        if ((canSee = (thisPlayers = players).get(player)) != null) {
            if (canSee) unrender(player);
            thisPlayers.remove(player);
        }
    }

    @Override
    public Collection<Player> getSeeingPlayers() {
        val seeingPlayers = new HashSet<Player>();
        for (val entry : players.entrySet()) if (entry.getValue()) seeingPlayers.add(entry.getKey());

        return seeingPlayers;
    }

    @Override
    public Collection<Player> getNotSeeingPlayers() {
        val notSeeingPlayers = new HashSet<Player>();
        for (val entry : players.entrySet()) if (!entry.getValue()) notSeeingPlayers.add(entry.getKey());

        return notSeeingPlayers;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Rendering
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Renders the entity for player making him see it.
     *
     * @param player player for whom to render the entity
     */
    protected abstract void render(Player player);

    /**
     * Unrenders the entity for player which means temporarily despawning the entity for him.
     *
     * @param player player for whom to unrender the entity
     */
    protected abstract void unrender(Player player);

    @Override
    public void attemptRerender(final Player player) {
        val sees = players.get(player);
        if (sees == null) return;

        if (sees) {
            if (!shouldSee(player)) unrender(player);
        } else if (shouldSee(player)) render(player);
    }


    @Override
    public void attemptRerenderForAll() {
        for (val entry : players.entrySet()) {
            val player = entry.getKey();

            if (entry.getValue()) { // sees
                if (!shouldSee(player)) unrender(player);
            } else if (shouldSee(player)) render(player);
        }
    }
}
