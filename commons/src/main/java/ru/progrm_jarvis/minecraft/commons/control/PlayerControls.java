package ru.progrm_jarvis.minecraft.commons.control;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;
import ru.progrm_jarvis.minecraft.commons.plugin.BukkitPluginContainer;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * An object which manages the player
 *
 * @param <P> type of plugin owning this player controls
 * @param <S> type of session created for player whose controls are managed
 * @param <E> tye of event to be called by the controls manager
 */
public interface PlayerControls<P extends Plugin, S extends PlayerControls.Session, E>
        extends BukkitPluginContainer<P>, PlayerContainer {

    /**
     * Starts the new controls session for the player.
     *
     * @param player player for whom to start the controls session
     * @return created controls session for the player (or the one he currently has in this player controls)
     *
     * @apiNote the session should be ended using {@link S#end()}
     */
    @NonNull S startSession(@NonNull Player player);

    @Override
    default void addPlayer(@NonNull final Player player) {
        startSession(player);
    }

    /**
     * Gets the current controls session of the specified player.
     *
     * @param player player for whom to get the controls sessions
     * @return optional of the player's current controls session or empty if it doesn't have one
     */
    @NonNull Optional<S> getSession(@NonNull Player player);

    @Override
    default void removePlayer(@NonNull final Player player) {
        getSession(player).ifPresent(Session::end);
    }

    @Override
    default boolean containsPlayer(@NonNull final Player player) {
        return getSession(player).isPresent();
    }

    /**
     * Subscribes the event handler on this player controls' events.
     *
     * @param eventHandler event handler to be used whenever an event is fired
     */
    void subscribe(@NonNull Consumer<E> eventHandler);

    /**
     * Unsubscribes the current event handler from this player controls' events.
     *
     * @return event handler which was unsubscribed or {@code null} if there was no subscribed event handler
     */
    @Nullable Consumer<E> unsubscribe();

    /**
     * Session of player controls, responsible for handling the player's controls
     */
    interface Session {

        /**
         * Gets the player manages by this controls session.
         *
         * @return MANAGED PLAYER
         */
        Player getPlayer();

        /**
         * Ends this player controls session.
         */
        void end();
    }
}
