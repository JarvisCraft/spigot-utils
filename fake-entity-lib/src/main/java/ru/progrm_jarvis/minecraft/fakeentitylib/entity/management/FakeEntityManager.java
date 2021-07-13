package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import lombok.NonNull;
import ru.progrm_jarvis.javacommons.annotation.DontOverrideEqualsAndHashCode;
import ru.progrm_jarvis.javacommons.collection.concurrent.ConcurrentCollections;
import ru.progrm_jarvis.minecraft.commons.plugin.BukkitPluginContainer;
import ru.progrm_jarvis.minecraft.commons.util.shutdown.Shutdownable;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import java.util.*;

/**
 * Basic class for entity management providing basic general functionality for it.
 *
 * @param <E> type of managed entity
 *
 * @implSpec its highly recommended (read <b>necessary</b>) for implementations
 * to store managed entities weakly so that un-managing entity manually is not required
 * as if there are no strong references on it the GC should collect it.
 */
@DontOverrideEqualsAndHashCode("EntityManagers are not data objects")
public interface FakeEntityManager<E extends FakeEntity> extends BukkitPluginContainer, Shutdownable {

    /**
     * Creates a new weak {@link Set} valid (and <i>recommended</i>) for storing entities
     *
     * @param <E> type of value stored
     * @return new weak {@link Set} for storing entities
     */
    static <E> Set<E> weakEntitySet() {
        return Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Creates a new weak concurrent {@link Set} valid (and <i>recommended</i>) for storing entities
     *
     * @param <E> type of value stored
     * @return new weak concurrent {@link Set} for storing entities
     */
    static <E> Set<E> concurrentWeakEntitySet() {
        return ConcurrentCollections.concurrentSetFromMap(new WeakHashMap<>());
    }

    /**
     * Creates a new weak {@link Set} valid (and <i>recommended</i>) for storing entities
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return new weak {@link Set} for storing entities
     */
    static <K, V> Map<K, V> weakEntityMap() {
        return new WeakHashMap<>();
    }

    /**
     * Creates a new weak concurrent {@link Map} valid (and <i>recommended</i>) for storing entities
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return new weak concurrent {@link Map} for storing entities
     */
    static <K, V> Map<K, V> concurrentWeakEntityMap() {
        return ConcurrentCollections.concurrentMap(new WeakHashMap<>());
    }

    /**
     * Gets amount of entities managed by this manager.
     *
     * @return amount of entities managed
     */
    int managedEntitiesSize();

    /**
     * Gets all entities managed by this manager.
     *
     * @return all entities managed by this manager
     *
     * @apiNote the returned collection is immutable and will prohibit any attempts to modify its contents
     */
    Collection<E> getManagedEntities();

    /**
     * Enables management of specified entity by this manager.
     *
     * @param entity entity to manage
     *
     * @apiNote Normally management should happen until {@link #unmanageEntity(FakeEntity)} is called with this entity
     * or (in most cases) if the entity is no longer non-weakly referenced.
     */
    void manageEntity(@NonNull E entity);

    /**
     * Disables management of specified entity by this manager.
     *
     * @param entity entity to manage
     *
     * @implSpec normally, this method should not necessarily be called
     * as this interface specification recommends storing entities weakly.
     */
    void unmanageEntity(@NonNull E entity);

    /**
     * Retrieves whether or not the specified entity is managed by this manager.
     *
     * @param entity entity to check
     * @return {@code true} if this entity manager manages the specified entity and {@code false} otherwise
     */
    boolean isManaged(final @NonNull E entity);

    /**
     * Removes the entity managed by this manager.
     * This is a logical equivalent of calling {@link #unmanageEntity(FakeEntity)} and {@link FakeEntity#remove()}
     *
     * @param entity entity to remove
     *
     * @apiNote if the entity is not managed by this manager then no exception should be thrown
     * but the removal should happen
     */
    default void remove(@NonNull E entity) {
        unmanageEntity(entity);

        entity.remove();
    }
}
