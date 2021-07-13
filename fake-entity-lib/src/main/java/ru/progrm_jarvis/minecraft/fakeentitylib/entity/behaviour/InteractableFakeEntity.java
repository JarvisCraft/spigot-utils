package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.fakeentitylib.entity.FakeEntity;

public interface InteractableFakeEntity extends FakeEntity {

    void handleInteraction(Player player, FakeEntityInteraction interaction);
}
