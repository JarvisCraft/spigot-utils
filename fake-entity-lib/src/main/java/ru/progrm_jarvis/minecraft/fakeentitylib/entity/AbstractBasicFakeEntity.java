package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.singletonList;

/**
 * Base for most common implementations of {@link BasicFakeEntity} containing player logic base.
 */
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractBasicFakeEntity extends AbstractPlayerContainingFakeEntity implements BasicFakeEntity {

    /**
     * Metadata of this fake entity
     */
    @Getter @Nullable WrappedDataWatcher metadata;

    /**
     * Velocity of this fake entity
     */
    @NonNull Vector velocity;

    /**
     * Whether optimized packets should use for moving the entity or not
     */
    boolean compactMoving;

    public AbstractBasicFakeEntity(final boolean global, final int viewDistance,
                                   final @NonNull Location location,
                                   final @NonNull Map<Player, Boolean> players,
                                   final @Nullable Vector velocity, final @Nullable WrappedDataWatcher metadata) {
        super(viewDistance, global, location, players);

        this.velocity = velocity == null ? new Vector() : velocity;
        this.metadata = metadata;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Metadata
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends metadata to all players seeing this entity creating packet if it has not yet been initialized.
     */
    protected abstract void sendMetadata();

    @Override
    public void setMetadata(final @NonNull WrappedDataWatcher metadata) {
        this.metadata = metadata.deepClone();

        sendMetadata();
    }

    @Override
    public void setMetadata(final @NonNull List<WrappedWatchableObject> metadata) {
        this.metadata = new WrappedDataWatcher(metadata);

        sendMetadata();
    }

    @Override
    public void setMetadata(final @NonNull Collection<WrappedWatchableObject> metadata) {
        setMetadata(new ArrayList<>(metadata));

        sendMetadata();
    }

    @Override
    public void setMetadata(final @NonNull WrappedWatchableObject... metadata) {
        setMetadata(Arrays.asList(metadata));

        sendMetadata();
    }

    @Override
    public void addMetadata(final Collection<WrappedWatchableObject> metadata) {
        final WrappedDataWatcher thisMetadata;
        if ((thisMetadata = this.metadata) == null) this.metadata = new WrappedDataWatcher(singletonList(metadata));
        else for (val metadatum : metadata) thisMetadata
                .setObject(metadatum.getWatcherObject(), metadatum.getRawValue());

        sendMetadata();
    }

    @Override
    public void addMetadata(final WrappedWatchableObject... metadata) {
        final WrappedDataWatcher thisMetadata;
        if ((thisMetadata = this.metadata) == null) this.metadata = new WrappedDataWatcher(Arrays.asList(metadata));
        else for (val metadatum : metadata) thisMetadata
                .setObject(metadatum.getWatcherObject(), metadatum.getRawValue());

        sendMetadata();
    }

    @Override
    public void removeMetadata(final Iterable<Integer> indexes) {
        final WrappedDataWatcher thisMetadata;
        if ((thisMetadata = metadata) == null) return;

        for (val index : indexes) thisMetadata.remove(index);

        sendMetadata();
    }

    @Override
    public void removeMetadata(final int... indexes) {
        final WrappedDataWatcher thisMetadata;
        if ((thisMetadata = metadata) == null) return;

        for (val index : indexes) thisMetadata.remove(index);

        sendMetadata();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Movement
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Performs the movement of this living fake entity by given deltas and yaw and pitch specified
     * not performing any checks such as 8-block limit of deltas or angle minimization.
     *
     * @param dx delta on X-axis
     * @param dy delta on Y-axis
     * @param dz delta on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     * @param sendVelocity {@code true} if velocity should be considered and {@code false} otherwise
     */
    protected abstract void performMoveLook(double dx, double dy, double dz,
                                            float yaw, float pitch, boolean sendVelocity);
    /**
     * Performs the movement of this living fake entity by given deltas and yaw and pitch specified
     * not performing any checks such as 8-block limit of deltas.
     *
     * @param dx delta on X-axis
     * @param dy delta on Y-axis
     * @param dz delta on Z-axis
     * @param sendVelocity {@code true} if velocity should be considered and {@code false} otherwise
     */
    protected abstract void performMove(double dx, double dy, double dz, boolean sendVelocity);

    /**
     * Performs the teleportation of this living fake entity to given coordinates changing yaw and pitch
     * not performing any checks such as using movement for less than 8-block deltas or angle minimization.
     *
     * @param x new location on X-axis
     * @param y new location on Y-axis
     * @param z new location on Z-axis
     * @param yaw new yaw
     * @param pitch new pitch
     * @param sendVelocity {@code true} if velocity should be considered and {@code false} otherwise
     */
    protected abstract void performTeleportation(double x, double y, double z,
                                                 float yaw, float pitch, boolean sendVelocity);

    /**
     * Performs the look by specified yaw and pitch.
     *
     * @param yaw new yaw
     * @param pitch new pitch
     */
    protected abstract void performLook(float yaw, float pitch);

    @Override
    public void move(final double dx, final double dy, final double dz, final float dYaw, final float dPitch) {
        if (compactMoving) performCompactMove(dx, dy, dz, dYaw, dPitch);
        else performNonCompactMove(dx, dy, dz, dYaw, dPitch);
    }

    protected void performCompactMove(final double dx, final double dy, final double dz,
                                      final float dYaw, final float dPitch) {
        val thisLocation = location;
        if (dx == 0 && dy == 0 && dz == 0) {
            if (dYaw != 0 || dPitch != 0) {
                final float yaw = thisLocation.getYaw() + dYaw, pitch = thisLocation.getPitch() + dPitch;

                performLook(yaw, pitch);

                thisLocation.setYaw(yaw);
                thisLocation.setPitch(pitch);
            }
        } else {
            final Vector thisVelocity;

            (thisVelocity = velocity).setX(dx);
            thisVelocity.setY(dy);
            thisVelocity.setZ(dz);
            // use teleportation if any of axises is above 8 blocks limit
            if (dx > 8 || dy > 8 || dz > 8) {
                final double x = thisLocation.getX() + dx, y = thisLocation.getY() + dy, z = thisLocation.getZ() + dz;
                final float yaw = thisLocation.getYaw() + dYaw, pitch = thisLocation.getPitch() + dPitch;

                performTeleportation(x, y, z, pitch, yaw, true);

                thisLocation.setX(x);
                thisLocation.setY(y);
                thisLocation.setZ(z);
                thisLocation.setYaw(yaw);
                thisLocation.setPitch(pitch);
            }
            // otherwise use move
            else {
                if (dYaw == 0 && dPitch == 0) performMove(dx, dy, dz, true);
                else {
                    performMoveLook(dx, dy, dz, dYaw, dPitch, true);

                    thisLocation.setYaw(thisLocation.getYaw() + dYaw);
                    thisLocation.setPitch(thisLocation.getPitch() + dPitch);
                }

                thisLocation.setX(thisLocation.getX() + dx);
                thisLocation.setY(thisLocation.getY() + dy);
                thisLocation.setZ(thisLocation.getZ() + dz);
            }

            thisVelocity.setX(0);
            thisVelocity.setY(0);
            thisVelocity.setZ(0);
        }
    }
    protected void performNonCompactMove(final double dx, final double dy, final double dz,
                                      final float dYaw, final float dPitch) {
        val thisLocation = location;

        var changeLocation = false;
        double x = Double.NaN;
        if (dx != 0) {
            changeLocation = true;

            thisLocation.setX(x = (thisLocation.getX() + dx));
        }
        double y = Double.NaN;
        if (dy != 0) {
            changeLocation = true;

            thisLocation.setY(y = (thisLocation.getY() + dy));
        }
        double z = Double.NaN;
        if (dz != 0) {
            changeLocation = true;

            thisLocation.setZ(z = (thisLocation.getZ() + dz));
        }

        var changeLook = false;
        float yaw = Float.NaN;
        if (dYaw != 0) {
            changeLook = true;

            thisLocation.setYaw(yaw = (thisLocation.getYaw() + dYaw));
        }
        float pitch = Float.NaN;
        if (dPitch != 0) {
            changeLook = true;

            thisLocation.setPitch(pitch = (thisLocation.getPitch() + dPitch));
        }

        if (changeLocation) performTeleportation(x, y, z, yaw, pitch, false);
        else if (changeLook) performLook(yaw, pitch);
    }

    @Override
    public void moveTo(final double x, final double y, final double z, final float yaw, final float pitch) {
        if (compactMoving) performCompactTeleportation(x, y, z, yaw, pitch, true);
        else performNonCompactTeleportation(x, y, z, yaw, pitch);
    }

    @Override
    public void teleport(final double x, final double y, final double z, final float yaw, final float pitch) {
        if (compactMoving) performCompactTeleportation(x, y, z, yaw, pitch, false);
        else performNonCompactTeleportation(x, y, z, yaw, pitch);
    }

    protected void performCompactTeleportation(final double x, final double y, final double z,
                                               final float yaw, final float pitch, final boolean sendVelocity) {
        final Location thisLocation;
        final double
                dx = x - (thisLocation = location).getX(),
                dy = y - thisLocation.getY(),
                dz = z - thisLocation.getZ();

        if (dx == 0 && dy == 0 && dz == 0) {
            if (yaw != thisLocation.getYaw() || pitch != thisLocation.getPitch()) {
                performLook(yaw, pitch);

                thisLocation.setYaw(yaw);
                thisLocation.setPitch(pitch);
            }
        } else {
            final Vector thisVelocity;
            (thisVelocity = velocity).setX(dx);
            thisVelocity.setY(dy);
            thisVelocity.setZ(dz);

            if (dx > 8 || dy > 8 || dz > 8) performTeleportation(x, y, z, yaw, pitch, sendVelocity);
            else if (yaw != thisLocation.getYaw() || pitch != thisLocation.getPitch()) {
                performMoveLook(dx, dy, dz, yaw, pitch, sendVelocity);

                thisLocation.setYaw(yaw);
                thisLocation.setPitch(pitch);
            } else performMove(dx, dy, dz, sendVelocity);

            thisLocation.setX(x);
            thisLocation.setY(y);
            thisLocation.setZ(z);

            thisVelocity.setX(0);
            thisVelocity.setY(0);
            thisVelocity.setZ(0);
        }
    }

    protected void performNonCompactTeleportation(final double x, final double y, final double z,
                                                  final float yaw, final float pitch) {
        var changeLocation = false;

        final Location thisLocation;
        if (x != (thisLocation = location).getX()) {
            changeLocation = true;

            thisLocation.setX(x);
        }
        if (y != thisLocation.getY()) {
            changeLocation = true;

            thisLocation.setY(y);
        }
        if (z != thisLocation.getZ()) {
            changeLocation = true;

            thisLocation.setZ(z);
        }
        var changeLook = false;
        if (yaw != thisLocation.getYaw()) {
            changeLook = true;

            thisLocation.setYaw(yaw);
        }
        if (pitch != thisLocation.getPitch()) {
            changeLook = true;

            thisLocation.setPitch(pitch);
        }

        if (changeLocation) performTeleportation(x, y, z, yaw, pitch, false);
        else if (changeLook) performLook(yaw, pitch);
    }

    @Override
    public void syncLocation() {
        final Location thisLocation;
        performTeleportation(
                (thisLocation = location).getX(), thisLocation.getY(), thisLocation.getZ(),
                thisLocation.getYaw(), thisLocation.getPitch(), false
        );
    }
}
