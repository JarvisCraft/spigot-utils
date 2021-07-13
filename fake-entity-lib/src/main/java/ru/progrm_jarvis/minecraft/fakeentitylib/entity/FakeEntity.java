package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

import java.util.Collection;

public interface FakeEntity extends PlayerContainer {

    /**
     * Gets the unique ID of this entity for use in player packets.
     *
     * @return Minecraft entity unique ID of this fake entity
     */
    int getEntityId();

    /**
     * Gets the world of this fake entity.
     *
     * @return world of this fake entity
     */
    World getWorld();

    /**
     * Gets location of this fake entity.
     *
     * @return location of this fake entity
     *
     * @apiNote changes to the returned location will not affect the fake entity
     */
    Location getLocation();

    /**
     * Gets the players associated with this fake entity.
     *
     * @return all players associated with this entity
     */
    Collection<? extends Player> getPlayers();

    /**
     * Gets whether this fake entity is visible or not.
     *
     * @return {@code true} if this fake entity is visible or {@code false} otherwise
     */
    boolean isVisible();

    /**
     * Makes this fake entity visible or not invisible.
     *
     * @param visible {@code true} if this fake entity should be visible or {@code false} if it should be invisible
     */
    void setVisible(boolean visible);

    /**
     * Removes the fake entity which guarantees that no other cleanups will be required.
     */
    void remove();
}
