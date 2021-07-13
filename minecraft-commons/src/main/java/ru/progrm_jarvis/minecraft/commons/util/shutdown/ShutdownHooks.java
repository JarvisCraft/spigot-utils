package ru.progrm_jarvis.minecraft.commons.util.shutdown;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.plugin.BukkitPluginShutdownUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An object holding all shutdown hooks of an object which should be called once it should bw shut down.
 * This object also guarantees that all hooks will be ran only once and repeated call
 */
public interface ShutdownHooks extends Shutdownable {

    /**
     * Adds a shutdown hook.
     *
     * @param hook shutdown hook to add
     * @return this shutdown hooks for chaining
     */
    @NonNull ShutdownHooks add(@NonNull Runnable hook);

    /**
     * Adds a shutdown hook.
     *
     * @param hookSupplier supplier to be used instantly to create a hook
     * @return this shutdown hooks for chaining
     *
     * @apiNote supplier is called instantly, not lazily
     */
    @NonNull ShutdownHooks add(@NonNull Supplier<Runnable> hookSupplier);

    /**
     * Adds a shutdown hook.
     *
     * @param objectSupplier supplier to create an object which wil be shut down
     * @param hookCreator function to create a hook
     * @param <T> type of the supplied object
     * @return this shutdown hooks for chaining
     *
     * @apiNote supplier and function are called instantly, not lazily
     */
    <T> T add(@NonNull Supplier<T> objectSupplier, @NonNull Function<T, Runnable> hookCreator);

    /**
     * Removes a shutdown hook.
     *
     * @param hook shutdown hook to remove
     * @return this shutdown hooks for chaining
     */
    @NonNull ShutdownHooks remove(@NonNull Runnable hook);

    /**
     * Registers these shutdown hooks as a Bukkit plugin shutdown hook.
     *
     * @param plugin plugin whose shutdown hook this is
     * @return this shutdown hooks for chaining
     */
    @NonNull ShutdownHooks registerBukkitShutdownHook(@NonNull Plugin plugin);

    /**
     * Unregisters these shutdown hooks as a Bukkit plugin shutdown hook.
     *
     * @return this shutdown hooks for chaining
     */
    @NonNull ShutdownHooks unregisterBukkitShutdownHook();

    /**
     * Calls all the hooks.
     */
    @Override
    void shutdown();

    /**
     * Retrieves whether or not {@link #shutdown()} was called.
     *
     * @return {@code true} if this was shut down and {@code false} otherwise
     */
    boolean isShutDown();

    /**
     * Creates a new shutdown hook instance.
     *
     * @return created shutdown hook instance
     */
    static ShutdownHooks create() {
        return new Simple();
    }

    /**
     * Creates new shutdown hook instance.
     *
     * @param parent object whose shutdown hooks those are
     * @return created shutdown hook instance
     */
    static ShutdownHooks create(final @NonNull Shutdownable parent) {
        return new Simple(parent);
    }

    /**
     * Creates new concurrent shutdown hook instance.
     *
     * @return created concurrent shutdown hook instance
     */
    static ShutdownHooks createConcurrent() {
        return new Concurrent();
    }

    /**
     * Creates new concurrent shutdown hook instance.
     *
     * @param parent object whose shutdown hooks those are
     * @return created concurrent shutdown hook instance
     */
    static ShutdownHooks createConcurrent(final @NonNull Shutdownable parent) {
        return new Concurrent(parent);
    }

    // equals and hashcode are specifically omitted due to object's mutability
    @ToString
    @FieldDefaults(level = AccessLevel.PROTECTED)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class Simple implements ShutdownHooks {

        final @Nullable Shutdownable parent;
        final @NonNull Deque<Runnable> shutdownHooks = new ArrayDeque<>();
        @Getter boolean shutDown = false;

        @Nullable Plugin bukkitPlugin;

        protected Simple() {
            this(null);
        }

        protected void checkState() {
            if (shutDown) throw new ObjectAlreadyShutDownException(parent);
        }

        @Override
        @NonNull public ShutdownHooks add(final @NonNull Runnable hook) {
            checkState();

            shutdownHooks.add(hook);

            return this;
        }

        @Override
        @NonNull public ShutdownHooks add(final @NonNull Supplier<Runnable> hookSupplier) {
            checkState();

            shutdownHooks.add(hookSupplier.get());

            return this;
        }

        @Override
        public <T> T add(final @NonNull Supplier<T> objectSupplier, final @NonNull Function<T, Runnable> hookCreator) {
            checkState();

            val object = objectSupplier.get();
            shutdownHooks.add(hookCreator.apply(object));

            return object;
        }

        @Override
        @NonNull public ShutdownHooks remove(final @NonNull Runnable hook) {
            checkState();

            shutdownHooks.remove(hook);

            return this;
        }

        @Override
        @NonNull public ShutdownHooks registerBukkitShutdownHook(final @NonNull Plugin plugin) {
            checkState();

            if (bukkitPlugin == null) {
                BukkitPluginShutdownUtil.addShutdownHook(bukkitPlugin = plugin, this);

                return this;
            } else {
                BukkitPluginShutdownUtil.removeShutdownHook(bukkitPlugin, this);
                BukkitPluginShutdownUtil.addShutdownHook(plugin, this);
            }

            return this;
        }

        @Override
        @NonNull public ShutdownHooks unregisterBukkitShutdownHook() {
            checkState();

            if (bukkitPlugin != null) {
                BukkitPluginShutdownUtil.removeShutdownHook(bukkitPlugin, this);
                bukkitPlugin = null;
            }

            return this;
        }

        @Override
        public void shutdown() {
            if (shutDown) return;

            shutDown = true;

            if (bukkitPlugin != null) {
                BukkitPluginShutdownUtil.removeShutdownHook(bukkitPlugin, this);
                bukkitPlugin = null;
            }

            for (val shutdownHook : shutdownHooks) shutdownHook.run();

            shutdownHooks.clear();
        }
    }

    // equals and hashcode are specifically omitted due to object's mutability
    @ToString
    @FieldDefaults(level = AccessLevel.PROTECTED)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class Concurrent implements ShutdownHooks {

        final @Nullable Shutdownable parent;
        final @NonNull Deque<Runnable> shutdownHooks = new ConcurrentLinkedDeque<>();
        AtomicBoolean shutDown = new AtomicBoolean();

        final @NonNull AtomicReference<Plugin> bukkitPlugin = new AtomicReference<>();

        protected Concurrent() {
            this(null);
        }

        protected void checkState() {
            if (shutDown.get()) throw new ObjectAlreadyShutDownException(parent);
        }

        @Override
        @NonNull public ShutdownHooks add(final @NonNull Runnable hook) {
            checkState();

            shutdownHooks.add(hook);

            return this;
        }

        @Override
        @NonNull public ShutdownHooks add(final @NonNull Supplier<Runnable> hookSupplier) {
            checkState();

            shutdownHooks.add(hookSupplier.get());

            return this;
        }

        @Override
        public <T> T add(final @NonNull Supplier<T> objectSupplier, final @NonNull Function<T, Runnable> hookCreator) {
            checkState();

            val object = objectSupplier.get();
            shutdownHooks.add(hookCreator.apply(object));

            return object;
        }

        @Override
        @NonNull public ShutdownHooks remove(final @NonNull Runnable hook) {
            checkState();

            shutdownHooks.remove(hook);

            return this;
        }

        @Override
        @NonNull public ShutdownHooks registerBukkitShutdownHook(final @NonNull Plugin plugin) {
            checkState();

            if (bukkitPlugin.compareAndSet(null, plugin)) BukkitPluginShutdownUtil.addShutdownHook(plugin, this);
            else {
                BukkitPluginShutdownUtil.removeShutdownHook(bukkitPlugin.get(), this);
                BukkitPluginShutdownUtil.addShutdownHook(plugin, this);
            }

            return this;
        }

        @Override
        @NonNull public ShutdownHooks unregisterBukkitShutdownHook() {
            checkState();

            val plugin = bukkitPlugin.get();
            if (plugin != null) {
                BukkitPluginShutdownUtil.removeShutdownHook(plugin, this);
                bukkitPlugin.compareAndSet(plugin, null);
            }

            return this;
        }

        @Override
        public void shutdown() {
            if (shutDown.compareAndSet(false, true)) {
                {
                    val plugin = bukkitPlugin.get();
                    if (plugin != null) {
                        BukkitPluginShutdownUtil.removeShutdownHook(plugin, this);
                        bukkitPlugin.set(null);
                    }
                }

                for (val shutdownHook : shutdownHooks) shutdownHook.run();

                shutdownHooks.clear();
            }
        }

        @Override
        public boolean isShutDown() {
            return shutDown.get();
        }
    }
}
