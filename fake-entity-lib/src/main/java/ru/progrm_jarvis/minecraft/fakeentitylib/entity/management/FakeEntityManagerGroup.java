package ru.progrm_jarvis.minecraft.fakeentitylib.entity.management;

import com.google.common.collect.ImmutableList;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistryRegistration;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;


/**
 * Facade grouping multiple {@link FakeEntityManager}s into a single one.
 * Its general methods delegate the calls to each of the managers.
 *
 * @param <E> type of managed entity
 */
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@PlayerRegistryRegistration(PlayerRegistryRegistration.Policy.MANUAL)
public class FakeEntityManagerGroup<E extends FakeEntity> extends AbstractSetBasedEntityManager<E> {

    @NonNull Collection<FakeEntityManager<E>> managers;

    public FakeEntityManagerGroup(final @NonNull Plugin plugin,
                                  final @NonNull Set<E> entities,
                                  final @NonNull Collection<BiFunction<Plugin, Set<E>,
                                          ? extends FakeEntityManager<E>>> managerCreators) {
        super(plugin, entities);

        //noinspection unchecked
        managers = ImmutableList.copyOf(managerCreators.stream()
                .map(managerCreator -> managerCreator.apply(plugin, entities))
                .toArray(FakeEntityManager[]::new)
        );
    }

    @SafeVarargs
    public FakeEntityManagerGroup(final @NonNull Plugin plugin,
                                  final @NonNull Set<E> entities,
                                  final @NonNull BiFunction<Plugin, Set<E>,
                                          ? extends FakeEntityManager<E>>... managerCreators) {
        this(plugin, entities, Arrays.asList(managerCreators));
    }

    @Override
    public void manageEntity(final @NonNull E entity) {
        super.manageEntity(entity);

        for (val manager : managers) manager.manageEntity(entity);
    }

    @Override
    public void unmanageEntity(final @NonNull E entity) {
        super.unmanageEntity(entity);

        for (val manager : managers) manager.unmanageEntity(entity);
    }

    @Override
    public void remove(final @NonNull E entity) {
        for (val manager : managers) manager.remove(entity);

        entity.remove();
    }

    @Override
    public void shutdown() {
        for (val manager : managers) manager.shutdown();

        super.shutdown();
    }
}
