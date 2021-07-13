package ru.progrm_jarvis.minecraft.commons.event;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility for fluent and comfortable registration of events.
 */
@UtilityClass
public class FluentBukkitEvents {

    /**
     * Plugin manager of Bukkit
     */
    private final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();

    /**
     * Event listeners groups stored by <i>Plugin -> Event-Type -> EventPriority</i>.
     */
    private final Map<ListenerConfiguration<?>, EventListenersGroup<?>> LISTENERS_GROUPS = new ConcurrentHashMap<>();

    /**
     * Creates an event listener registration for fluent registration of the event listener(s).
     *
     * @param eventType type of the event handled to use for registration
     * @param <E> type of the event handled
     * @return event listener registration for fluent registration of the event listener(s)
     */
    public <E extends Event> EventListenerRegistration<E> on(final @NonNull Class<E> eventType) {
        return new EventListenerRegistration<>(eventType);
    }

    @Data
    @Accessors(chain = true, fluent = true)
    public static final class EventListenerRegistration<E extends Event> {

        /**
         * Type of event registered
         */
        @NonNull Class<E> type;

        /**
         * Plugin to use for event registration
         */
        @NonNull Plugin plugin;

        /**
         * Priority of the event's handler
         */
        @NonNull EventPriority priority = EventPriority.NORMAL;

        /**
         * Instantiates new event listener registration for the specified event type.
         *
         * @param type type of event handled
         */
        public EventListenerRegistration(final @NonNull Class<E> type) {
            this.type = type;
        }

        /**
         * Gets the event listeners group for this event listeners registration's configuration.
         *
         * @return event listeners group for this event listener registration's configuration
         */
        @SuppressWarnings("unchecked")
        private EventListenersGroup<E> getListenersGroup() {
            checkNotNull(plugin, "plugin has not been set");

            return (EventListenersGroup<E>) LISTENERS_GROUPS.computeIfAbsent(
                    new ListenerConfiguration<>(plugin, type, priority),
                    configuration -> new EventListenersGroup<>((ListenerConfiguration<E>) configuration)
            );
        }

        /**
         * Registers the event.
         *
         * @param listener listener to use for event handling
         * @return unregister to use for event unregistration
         */
        public Shutdownable register(final @NonNull Consumer<E> listener) {
            val listenersGroup = getListenersGroup();
            listenersGroup.addListener(listener);

            return () -> listenersGroup.removeListener(listener);
        }
    }

    /**
     * Group of event listeners having the same parameters.
     *
     * @param <E> type of handled event
     */
    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class EventListenersGroup<E extends Event> implements Listener, EventExecutor {

        /**
         * Plugin managing these event listeners
         */
        @NonNull ListenerConfiguration<E> configuration;

        /**
         * Event listeners in this event listener group's dequeue
         */
        @Getter(AccessLevel.NONE) @NonNull Deque<Consumer<E>> eventListeners = new ConcurrentLinkedDeque<>();

        /**
         * Adds the listener to the deque of handled listeners for the event.
         *
         * @param listener listener to add to handling dequeue
         */
        private void addListener(final @NonNull Consumer<E> listener) {
            if (eventListeners.isEmpty()) {
                PLUGIN_MANAGER.registerEvent(
                        configuration.getType(), this, configuration.getPriority(), this, configuration.getPlugin()
                );
                LISTENERS_GROUPS.putIfAbsent(configuration, this);
            }

            eventListeners.add(listener);
        }

        /**
         * Removes the listener from the deque of handled listeners for the event.
         *
         * @param listener listener to remove from handling dequeue
         */
        private void removeListener(final @NonNull Consumer<E> listener) {
            eventListeners.remove(listener);

            if (eventListeners.isEmpty()) {
                HandlerList.unregisterAll(this);
                LISTENERS_GROUPS.remove(configuration);
            }
        }

        @Override
        public void execute(final @NotNull Listener listener, final Event event) {
            if (configuration.getType().isAssignableFrom(event.getClass())) {
                @SuppressWarnings("unchecked") val castEvent = (E) event;
                for (val eventListener : eventListeners) eventListener.accept(castEvent);
            }
        }
    }

    @Value
    private static class ListenerConfiguration<E> {
        @NonNull Plugin plugin;
        @NonNull Class<E> type;
        @NonNull EventPriority priority;
    }
}
