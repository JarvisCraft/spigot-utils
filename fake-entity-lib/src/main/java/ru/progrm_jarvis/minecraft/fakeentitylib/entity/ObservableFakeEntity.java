package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.val;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface ObservableFakeEntity extends FakeEntity {

    /**
     * Gets view distance for this fake entity. This may be not present in which case this returns {@code -1}.
     *
     * @return non-negative view distance for this fake entity if present and {@code -1} otherwise
     */
    int getViewDistance();

    /**
     * Returns {@code true} if the player has this fake entity rendered and {@code false} otherwise.
     *
     * @apiNote this method also returns {@code false} if the player specified is not associated with this fake entity
     *
     * @param player player to check
     * @return whether or not the player has this fake entity rendered
     */
    boolean isRendered(Player player);

    /**
     * Returns {@code true} if the player can theoretically see this entity and {@code false} otherwise.
     *
     * @apiNote method does not check whether the player is managed by this entity
     *
     * @param player player to check for ability to see this fake entity
     * @return whether or not the player can see this fake entity
     */
    boolean shouldSee(Player player);

    /**
     * Gets all players who are related to this fake entity
     * and are seeing it at the moment (have it rendered).
     *
     * @return all players who have this fake entity rendered at the moment
     */
    Collection<Player> getSeeingPlayers();

    /**
     * Gets all players who are related to this fake entity
     * and are not seeing it at the moment (don't have it rendered).
     *
     * @return all players who don't have this fake entity rendered at the moment
     */
    Collection<Player> getNotSeeingPlayers();

    /**
     * Attempt to rerender this fake entity for player specified.
     * Rerendering means rendering if the player does not see the entity although he should
     * or calling unrender if the player sees the entity but should not.
     *
     * @param player player for whom to attempt to rerender this entity
     */
    void attemptRerender(final Player player);

    /**
     * Attempts to rerender this fake entity for all players associated with it.
     * This logically means calling {@link #attemptRerender(Player)} on each player from {@link #getPlayers()}
     * although implementations are expected to provide some optimizations of this.
     *
     * @implNote may be ineffective as players may be stored in a map containing information on them being rendered
     */
    default void attemptRerenderForAll() {
        for (val player : getPlayers()) attemptRerender(player);
    }
}
