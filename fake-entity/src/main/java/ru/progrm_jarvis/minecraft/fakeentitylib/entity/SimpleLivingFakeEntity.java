package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * A simple living entity self-sustained for direct usage.
 */
@ToString
// Entities are seriously mutable and most things may overlap in different cases so JDK hashcode is used
@FieldDefaults(level = AccessLevel.PROTECTED)
public class SimpleLivingFakeEntity extends AbstractBasicFakeEntity {

    ///////////////////////////////////////////////////////////////////////////
    // Basic entity data
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Unique entity ID by which it should be identified in all packets.
     */
    @Getter final int entityId; // id should not be generated before all checks are performed

    /**
     * This fake entity's UUID
     */
    @Nullable final UUID uuid; // may be null as it is not required

    /**
     * Type of this entity
     */
    final EntityType type; // type of entity

    ///////////////////////////////////////////////////////////////////////////
    // General for FakeEntity
    ///////////////////////////////////////////////////////////////////////////

    /**
     * PLayers related to this fake entity
     */
    @NonNull final Map<Player, Boolean> players;

    /**
     * Whether or not this fake entity is global
     */
    @Getter final boolean global;

    /**
     * View distance for this entity or {@code -1} if none
     */
    @Getter int viewDistance;

    ///////////////////////////////////////////////////////////////////////////
    // Entity changing parameters
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Location of this fake entity
     */
    @NonNull @Getter final Location location;

    /**
     * Head pitch of this fake entity
     */
    float headPitch;

    /**
     * Metadata of this fake entity
     */
    @Nullable @Getter WrappedDataWatcher metadata;

    // packets should not be created before id is generated

    /**
     * Packet used for spawning this fake entity
     */
    WrapperPlayServerSpawnEntityLiving spawnPacket;

    /**
     * Packet used for despawning this fake entity
     */
    WrapperPlayServerEntityDestroy despawnPacket;

    // packets which should be initialized only when first needed

    /**
     * Packet used for updating this fake entity's metadata
     */
    @NonFinal WrapperPlayServerEntityMetadata metadataPacket;

    /**
     * Packet used for moving this fake entity (not more than 8 blocks per axis) without modifying head rotation
     */
    WrapperPlayServerRelEntityMove movePacket;

    /**
     * Packet used for moving this fake entity (not more than 8 blocks per axis) and modifying head rotation
     */
    WrapperPlayServerRelEntityMoveLook moveLookPacket;

    /**
     * Packet used for teleporting this fake entity
     */
    WrapperPlayServerEntityTeleport teleportPacket;

    /**
     * Packet used for fake entity velocity
     */
    WrapperPlayServerEntityVelocity velocityPacket;

    /**
     * Difference between the actual entity <i>x</i> and its visible value
     */
    double xOffset,
    /**
     * Difference between the actual entity <i>y</i> and its visible value
     */
    yOffset,
    /**
     * Difference between the actual entity <i>z</i> and its visible value
     */
    zOffset;

    /**
     * Difference between the actual entity <i>yaw</i> and its visible value
     */
    float yawOffset = 0,

    /**
     * Difference between the actual entity <i>pitch</i> and its visible value
     */
    pitchOffset = 0,

    /**
     * Difference between the actual entity <i>head pitch</i> and its visible value
     */
    headPitchDelta = 0;

    @Builder
    public SimpleLivingFakeEntity(final int entityId, @Nullable final UUID uuid, @NonNull final EntityType type,
                                  @NonNull final Map<Player, Boolean> players,
                                  final boolean global, final int viewDistance,
                                  boolean visible,
                                  @NonNull final Location location, float headPitch,
                                  @Nullable final Vector velocity, @Nullable final WrappedDataWatcher metadata) {
        super(global, viewDistance, location, players, velocity, metadata);

        // setup fields

        this.entityId = entityId;
        this.uuid = uuid;
        this.type = type;

        this.players = players;
        this.global = global;
        this.viewDistance = Math.max(-1, viewDistance);
        this.visible = visible;

        this.location = location;
        this.headPitch = headPitch;

        this.metadata = metadata;

        // setup packets

        spawnPacket = new WrapperPlayServerSpawnEntityLiving();
        spawnPacket.setEntityID(this.entityId);
        spawnPacket.setType(type);
        if (uuid != null) spawnPacket.setUniqueId(uuid);

        despawnPacket = new WrapperPlayServerEntityDestroy();
        despawnPacket.setEntityIds(new int[]{this.entityId});
    }

    /**
     * Spawns the entity for player without performing any checks
     * such as player containment checks or spawn packet actualization.
     *
     * @param player player to whom to spawn this entity
     */
    protected void performSpawnNoChecks(final Player player) {
        spawnPacket.sendPacket(player);
    }

    /**
     * Despawns the entity for player without performing any checks
     * such as player containment checks or spawn packet actualization.
     *
     * @param player player to whom to despawn this entity
     */
    protected void performDespawnNoChecks(final Player player) {
        despawnPacket.sendPacket(player);
    }

    protected void actualizeSpawnPacket() {
        spawnPacket.setX(location.getX() + xOffset);
        spawnPacket.setY(location.getY() + yOffset);
        spawnPacket.setZ(location.getZ() + zOffset);

        spawnPacket.setPitch(location.getPitch() + pitchOffset);
        spawnPacket.setYaw(location.getYaw() + yawOffset);
        spawnPacket.setHeadPitch(headPitch + headPitchDelta);

        if (velocity != null) {
            spawnPacket.setVelocityX(velocity.getX());
            spawnPacket.setVelocityY(velocity.getY());
            spawnPacket.setVelocityZ(velocity.getZ());
        } else {
            spawnPacket.setVelocityX(0);
            spawnPacket.setVelocityY(0);
            spawnPacket.setVelocityZ(0);
        }

        if (metadata != null) spawnPacket.setMetadata(metadata);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spawning / Despawning
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void spawn() {
        if (visible) {
            actualizeSpawnPacket();

            for (val entry : players.entrySet()) if (entry.getValue()) performSpawnNoChecks(entry.getKey());
        }
    }

    @Override
    public void despawn() {
        if (visible) {
            for (val entry : players.entrySet()) if (entry.getValue()) performDespawnNoChecks(entry.getKey());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Movement
    ///////////////////////////////////////////////////////////////////////////

    protected boolean hasVelocity() {
        return velocity.length() != 0;
    }

    /**
     * Updates the velocity packet initializing it if it haven;t been initialized.
     *
     * @apiNote call to this method guarantees that {@link #velocityPacket} won't be {@link null} after it
     */
    protected void actualizeVelocityPacket() {
        if (velocityPacket == null) {
            velocityPacket = new WrapperPlayServerEntityVelocity();
            velocityPacket.setEntityID(entityId);
        }

        velocityPacket.setVelocityX(velocity.getX());
        velocityPacket.setVelocityY(velocity.getY());
        velocityPacket.setVelocityZ(velocity.getZ());
    }

    /**
     * Performs the movement of this living fake entity by given deltas and yaw and pitch specified
     * not performing any checks such as 8-block limit of deltas or angle minimization.
     *
     * @param dx delta on X-axis
     * @param dy delta on Y-axis
     * @param dz delta on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     */
    @Override
    @SuppressWarnings("Duplicates")
    protected void performMove(final double dx, final double dy, final double dz, final float yaw, final float pitch) {
        if (visible) {
            if (pitch == 0 && yaw == 0) {
                if (movePacket == null) {
                    movePacket = new WrapperPlayServerRelEntityMove();
                    movePacket.setEntityID(entityId);
                }

                movePacket.setDx(dx);
                movePacket.setDy(dy);
                movePacket.setDz(dz);

                boolean hasVelocity = hasVelocity();
                if (hasVelocity) actualizeVelocityPacket();

                for (val entry : players.entrySet()) if (entry.getValue()) {
                    val player = entry.getKey();

                    if (hasVelocity) velocityPacket.sendPacket(player);
                        movePacket.sendPacket(player);
                }
            } else {
                if (moveLookPacket == null) {
                    moveLookPacket = new WrapperPlayServerRelEntityMoveLook();
                    moveLookPacket.setEntityID(entityId);
                }

                moveLookPacket.setDx(dx);
                moveLookPacket.setDy(dy);
                moveLookPacket.setDz(dz);
                moveLookPacket.setYaw(yaw);
                moveLookPacket.setPitch(pitch);

                boolean hasVelocity = hasVelocity();
                if (hasVelocity) actualizeVelocityPacket();

                for (val entry : players.entrySet()) if (entry.getValue()) {
                    val player = entry.getKey();

                    if (hasVelocity) velocityPacket.sendPacket(player);
                    moveLookPacket.sendPacket(player);
                }
            }
        }
    }

    /**
     * Performs the teleportation of this living fake entity to given coordinates changing yaw and pitch
     * not performing any checks such as using movement for less than 8-block deltas or angle minimization.
     *
     * @param x new location on X-axis
     * @param y new location on Y-axis
     * @param z new location on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     */
    @Override
    @SuppressWarnings("Duplicates")
    protected void performTeleportation(final double x, final double y, final double z,
                                        final float yaw, final float pitch) {
        if (visible) {
            if (teleportPacket == null) {
                teleportPacket = new WrapperPlayServerEntityTeleport();
                teleportPacket.setEntityID(entityId);
            }

            teleportPacket.setX(x + xOffset);
            teleportPacket.setY(y + yOffset);
            teleportPacket.setZ(z + zOffset);
            teleportPacket.setYaw(yaw + yawOffset);
            teleportPacket.setPitch(pitch + pitchOffset);

            boolean hasVelocity = hasVelocity();
            if (hasVelocity) actualizeVelocityPacket();

            for (val entry : players.entrySet()) if (entry.getValue()) {
                val player = entry.getKey();

                if (hasVelocity) velocityPacket.sendPacket(player);
                teleportPacket.sendPacket(player);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends metadata to all players seeing this entity creating packet if it has not yet been initialized.
     */
    @Override
    protected void sendMetadata() {
        if (visible) {
            if (metadata == null) return;
            if (metadataPacket == null) {
                metadataPacket = new WrapperPlayServerEntityMetadata();
                metadataPacket.setEntityID(entityId);
            }
            metadataPacket.setMetadata(metadata.getWatchableObjects());

            for (val entry : players.entrySet()) if (entry.getValue()) metadataPacket.sendPacket(entry.getKey());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Rendering
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void render(final Player player) {
        actualizeSpawnPacket();
        performSpawnNoChecks(player);

        players.put(player, true);
    }

    @Override
    public void unrender(final Player player) {
        performDespawnNoChecks(player);

        players.put(player, false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Visibility
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setVisible(final boolean visible) {
        if (this.visible == visible) return;

        this.visible = visible;

        if (visible) spawn();
        else despawn();
    }

    @Override
    public void remove() {
        despawn();

        players.clear();
    }
}
