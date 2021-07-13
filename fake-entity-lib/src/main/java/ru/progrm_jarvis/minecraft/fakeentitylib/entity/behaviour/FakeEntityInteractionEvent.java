package ru.progrm_jarvis.minecraft.fakeentitylib.entity.behaviour;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class FakeEntityInteractionEvent extends PlayerEvent {

    @Getter private static final HandlerList handlerList = new HandlerList();

    @NonNull private InteractableFakeEntity entity;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public FakeEntityInteractionEvent(final @NonNull Player who, final @NonNull InteractableFakeEntity entity) {
        super(who);

        this.entity = entity;
    }
}
