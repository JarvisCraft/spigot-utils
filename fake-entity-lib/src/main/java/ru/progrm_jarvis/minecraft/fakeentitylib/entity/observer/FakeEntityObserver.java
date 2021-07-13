package ru.progrm_jarvis.minecraft.fakeentitylib.entity.observer;

import org.bukkit.plugin.Plugin;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.ObservableFakeEntity;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.management.FakeEntityManager;

/**
 * An object responsible for entity observation.
 * Observation is the process of controlling the entity visibility for players.
 *
 * @param <E> type of entity managed
 */
public interface FakeEntityObserver<E extends ObservableFakeEntity> extends FakeEntityManager<E> {
}
