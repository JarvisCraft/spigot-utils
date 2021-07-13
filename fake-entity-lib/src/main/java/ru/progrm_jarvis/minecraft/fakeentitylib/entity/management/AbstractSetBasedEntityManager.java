package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.ShutdownHooks;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Simple abstract {@link FakeEntityManager} storing entities in a weak, optionally concurrent set.
 *
 * @param <E> type of entities stored
 */
@ToString
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractSetBasedEntityManager<E extends FakeEntity> implements FakeEntityManager<E> {

    @NonNull Plugin plugin;
    @ToString.Exclude @NonNull Set<E> entities;
    @ToString.Exclude @NonNull Set<E> entitiesView;

    @Delegate(types = Shutdownable.class) @NonNull ShutdownHooks shutdownHooks;

    public AbstractSetBasedEntityManager(final @NonNull Plugin plugin, final @NonNull Set<E> entities) {
        this.plugin = plugin;
        this.entities = entities;
        this.entitiesView = Collections.unmodifiableSet(entities);

        shutdownHooks = ShutdownHooks.createConcurrent(this)
                .registerBukkitShutdownHook(plugin);
    }

    /**
     * Constructs a new AbstractSetBasedEntityManager based on weak set with optional concurrency
     *
     * @param plugin parent plugin of this manager
     * @param concurrent whether or not this map should be thread-safe
     */
    public AbstractSetBasedEntityManager(final @NonNull Plugin plugin, final boolean concurrent) {
        this(plugin, concurrent ? FakeEntityManager.concurrentWeakEntitySet() : FakeEntityManager.weakEntitySet());
    }

    @Override
    public Plugin getBukkitPlugin() {
        return plugin;
    }

    @Override
    public int managedEntitiesSize() {
        return entities.size();
    }

    @Override
    public boolean isManaged(final @NonNull E entity) {
        return entities.contains(entity);
    }

    @Override
    public Collection<E> getManagedEntities() {
        return entitiesView;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void manageEntity(final @NonNull E entity) {
        entities.add(entity);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void unmanageEntity(final @NonNull E entity) {
        entities.remove(entity);
    }
}
