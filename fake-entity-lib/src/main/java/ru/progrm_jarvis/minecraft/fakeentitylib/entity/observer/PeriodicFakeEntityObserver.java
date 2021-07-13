package ru.progrm_jarvis.minecraft.fakeentitylib.entity.observer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.schedule.task.AbstractSchedulerRunnable;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.ShutdownHooks;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.ObservableFakeEntity;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.AbstractSetBasedEntityManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.progrm_jarvis.minecraft.commons.event.FluentBukkitEvents.on;
import static ru.progrm_jarvis.minecraft.commons.util.hack.PreSuperCheck.beforeSuper;

@ToString
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class PeriodicFakeEntityObserver<E extends ObservableFakeEntity>
        extends AbstractSetBasedEntityManager<E> implements FakeEntityObserver<E> {

    Set<RedrawEntitiesRunnable> tasks = new HashSet<>();
    Lock lock = new ReentrantLock();

    boolean global;
    long interval;
    boolean async;
    int minEntitiesForNewThread;
    int maxThreads;

    Supplier<Set<E>> entitiesSetSupplier;

    @Builder
    public PeriodicFakeEntityObserver(final @NonNull Plugin plugin, final boolean concurrent,
                                      boolean global, final long interval, final boolean async,
                                      final int minEntitiesForNewThread, final int maxThreads,
                                      final @NonNull Supplier<Set<E>> entitiesSetSupplier) {
        super(plugin, beforeSuper(concurrent,
                () -> checkArgument(interval > 0, "interval should be positive"),
                () -> checkArgument(minEntitiesForNewThread > 0, "minEntitiesForNewThread should be positive"),
                () -> checkArgument(maxThreads > 0, "maxThreads should be positive")
        ));

        this.global = global;
        this.interval = interval;
        this.async = async;
        this.minEntitiesForNewThread = minEntitiesForNewThread;
        this.maxThreads = maxThreads;

        this.entitiesSetSupplier = entitiesSetSupplier;

        final ShutdownHooks shutdownHooks;
        (shutdownHooks = this.shutdownHooks).add(() -> {
            lock.lock();
            try {
                for (val task : tasks) task.cancel();
            } finally {
                lock.unlock();
            }
        });

        if (global) shutdownHooks
                .add(on(PlayerJoinEvent.class)
                        .plugin(plugin)
                        .register(event -> addPlayer(event.getPlayer()))::shutdown)
                .add(on(PlayerQuitEvent.class)
                        .plugin(plugin)
                        .register(event -> removePlayer(event.getPlayer()))::shutdown);

        shutdownHooks.add(on(PlayerRespawnEvent.class)
                .plugin(plugin)
                .register(event -> {
                    final Player player;
                    removePlayer(player = event.getPlayer());
                    addPlayer(player);
                })::shutdown);
    }

    protected void addPlayer(final @NonNull Player player) {
        for (val entity : entities) entity.addPlayer(player);
    }

    protected void removePlayer(final @NonNull Player player) {
        for (val entity : entities) entity.removePlayer(player);
    }

    protected RedrawEntitiesRunnable getRedrawEntitiesRunnable() {
        lock.lock();
        try {
            if (tasks.isEmpty()) return newRedrawEntitiesRunnable();
            else {
                RedrawEntitiesRunnable minRunnable = null;
                Integer minEntitiesInRunnable = null;
                for (val task : tasks) {
                    val taskEntitiesSize = task.size();

                    // if task has no even reached its entity minimum then use it
                    if (taskEntitiesSize < minEntitiesForNewThread) return task;
                        // else assign min values if the task is the smallest
                    else if (minEntitiesInRunnable == null || taskEntitiesSize < minEntitiesInRunnable) {
                        minEntitiesInRunnable = taskEntitiesSize;
                        minRunnable = task;
                    }
                }

                // add new thread if there is yet space for it or the minimal if not
                return tasks.size() < maxThreads ? newRedrawEntitiesRunnable() : minRunnable;
            }
        } finally {
            lock.unlock();
        }
    }

    protected RedrawEntitiesRunnable newRedrawEntitiesRunnable() {
        val runnable = new RedrawEntitiesRunnable();
        if (async) runnable.runTaskTimerAsynchronously(plugin, interval, interval);
        else runnable.runTaskTimer(plugin, interval, interval);

        tasks.add(runnable);

        return runnable;
    }

    @Override
    public void manageEntity(final @NonNull E entity) {
        super.manageEntity(entity);
        getRedrawEntitiesRunnable().addEntity(entity);
    }

    @Override
    public void unmanageEntity(final @NonNull E entity) {
        super.unmanageEntity(entity);
        lock.lock();
        try {
            val iterator = tasks.iterator();
            while (iterator.hasNext()) {
                val task = iterator.next();
                if (task.removeEntity(entity)) {
                    if (task.isEmpty()) {
                        iterator.remove();
                        task.cancel();
                    }

                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @ToString
    @EqualsAndHashCode(callSuper = true)
    protected class RedrawEntitiesRunnable extends AbstractSchedulerRunnable {

        protected final Collection<E> entities = entitiesSetSupplier.get();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        public int size() {
            return entities.size();
        }

        public boolean isEmpty() {
            return entities.isEmpty();
        }

        public void addEntity(final E entity) {
            lock.writeLock().lock();
            try {
                if (global && entity.isGlobal()) entity.addOnlinePlayers();
                entities.add(entity);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public boolean removeEntity(final E entity) {
            lock.writeLock().lock();
            try {
                return entities.remove(entity);
            } finally {
                lock.writeLock().unlock();
            }
        }

        @Override
        public void run() {
            lock.readLock().lock();
            try {
                for (val entity : entities) entity.attemptRerenderForAll();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
}
