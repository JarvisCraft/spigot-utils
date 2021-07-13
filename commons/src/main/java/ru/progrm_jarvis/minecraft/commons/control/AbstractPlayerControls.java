package ru.progrm_jarvis.minecraft.commons.control;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Abstract implementation of {@link PlayerControls} providing its common mechanisms.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractPlayerControls<P extends Plugin, S extends AbstractPlayerControls.Session, E>
        implements PlayerControls<P, S, E> {

    /**
     * Plugin whose player controls those are.
     */
    @NonNull P plugin;

    /**
     * Whether this control session is a <i>global player container</i> or not
     */
    @Getter boolean global;

    /**
     * Map of player's currently managed by this player controls and their current active sessions
     */
    @NonNull Map<@NonNull Player, @NonNull S> sessions;

    /**
     * Reference to the currently set event handler of this player controls
     */
    @NonNull AtomicReference<Consumer<E>> eventHandler = new AtomicReference<>();

    @Override
    public P getBukkitPlugin() {
        return plugin;
    }

    // a more optimal solution without allocating unneeded Optional objects
    @Override
    public boolean containsPlayer(@NonNull final Player player) {
        return sessions.containsKey(player);
    }

    // a more optimal solution without allocating unneeded Optional objects
    @Override
    public void removePlayer(@NonNull final Player player) {
        val session = sessions.get(player);
        if (session != null) session.end();
    }

    @Override
    public Collection<? extends Player> getPlayers() {
        return sessions.keySet();
    }

    @Override
    @NonNull public S startSession(@NonNull final Player player) {
        val session = createSession(player);
        sessions.put(player, session);

        return session;
    }

    @Override
    public @NonNull Optional<S> getSession(@NonNull final Player player) {
        return Optional.ofNullable(sessions.get(player));
    }

    /**
     * Creates the controls session for the player specified.
     *
     * @param player player for whom to initialize the controls session
     * @return created controls session
     *
     * @implSpec implementations are not required to add the created session to {@link #sessions}
     * as this is done by this method's caller
     *
     * @see Session#startSession(Player) this method's default caller
     */
    @NonNull protected abstract S createSession(Player player);

    /**
     * Finalizer called in {@link S#end()} in order to cleanup everything needed when the player end his session.
     *
     * @param session session to release
     * @implSpec should <b>not</b> call to {@link S#end()} as this will (most definitely) lead to infinite recursion
     *
     * @implSpec implementations are not required to remove the session from {@link #sessions}
     * as this is done by this method's caller
     *
     * @see Session#end() this method's default caller
     */
    protected void releaseControls(@NonNull final S session) {}

    @Override
    public void subscribe(@NonNull final Consumer<E> eventHandler) {
        this.eventHandler.set(eventHandler);
    }

    @Nullable
    @Override
    public Consumer<E> unsubscribe() {
        return eventHandler.getAndSet(null);
    }

    /**
     * Default session object to be used with {@link AbstractPlayerControls}.
     */
    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
    public abstract class Session implements PlayerControls.Session {

        /**
         * Player whose session this one is
         */
        @Getter Player player;

        @Override
        public void end() {
            //noinspection unchecked
            releaseControls((S) this);
            sessions.remove(player);
        }
    }
}
