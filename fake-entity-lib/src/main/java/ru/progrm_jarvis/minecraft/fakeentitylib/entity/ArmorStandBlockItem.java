package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.javacommons.ownership.annotation.Own;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand.ArmorStandFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.*;
import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand.ArmorStandFlag.*;
import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand.armorStandFlags;
import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.ArmorStand.headRotation;
import static ru.progrm_jarvis.minecraft.commons.nms.metadata.MetadataGenerator.Entity.*;

/**
 * A fake small (or very small) movable block-item which can be normally rotated over all axises
 * and have floating point coordinates. This block is displayed as an item on head of invisible armor stand.
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ArmorStandBlockItem extends SimpleLivingFakeEntity {

    protected static final double PIXEL_SIZE = 0x1p-4,
            HALF_PIXEL_SIZE = 0x1p-5,
            ARMOR_STAND_BODY_HEIGHT = (16 + 8) * PIXEL_SIZE,
            ARMOR_STAND_HEAD_ROOT_OFFSET = ARMOR_STAND_BODY_HEIGHT - HALF_PIXEL_SIZE,
            ITEM_CENTER_Y_OFFSET = 3 * PIXEL_SIZE + HALF_PIXEL_SIZE;
    // offset of the item center from the rotation center

    final boolean small, marker;
    final double itemCenterYOffset;

    @NotNull Offset offset;
    //double xOffset, yOffset, zOffset;

    /**
     * Rotation of this block
     */
    @Nullable Vector3F rotation;

    /**
     * Item displayed by this block
     */
    @NonNull ItemStack item;

    /**
     * Packet used for displaying this block's displayed item
     */
    WrapperPlayServerEntityEquipment equipmentPacket;

    /**
     * Initializes a newly created armor stand block-item from parameters given.
     *
     * @param uuid unique entity ID of this block-item entity
     * @param playersMap map to be used as backend for this block-item entity
     * @param global whether this block-item is global (the value returned by {@link #isGlobal()})
     * @param visible whether this block-item is initially be visible
     * @param viewDistance view distance of this block-item
     * @param location location of this block-item
     * @param rotation rotation of this block item
     * @param itemCenterYOffset offset of the item center on Y-axis
     * @param offset offset of the entity from its logical center
     * @param small whether this block-item is small
     * @param marker whether this block-item is marker
     * @param item item to be displayed by this block-item
     */
    protected ArmorStandBlockItem(final @Nullable UUID uuid,
                                  final @NotNull Map<@NotNull Player, @NotNull Boolean> playersMap,
                                  final boolean global, final int viewDistance, final boolean visible,
                                  final @NotNull Location location, final @NotNull Vector3F rotation,
                                  final double itemCenterYOffset, final @NotNull Offset offset,
                                  final boolean small, final boolean marker, final @NotNull ItemStack item) {
        super(
                NmsUtil.nextEntityId(), uuid, EntityType.ARMOR_STAND,
                playersMap, global, viewDistance, visible, location, 0, null, createMetadata(rotation, small, marker)
        );

        this.small = small;
        this.marker = marker;
        this.itemCenterYOffset = itemCenterYOffset;

        this.rotation = rotation;
        this.offset = offset;

        final WrapperPlayServerEntityEquipment thisEquipmentPacket;
        equipmentPacket = thisEquipmentPacket = new WrapperPlayServerEntityEquipment();
        thisEquipmentPacket.setEntityID(entityId);
        thisEquipmentPacket.setSlot(EnumWrappers.ItemSlot.HEAD);
        thisEquipmentPacket.setItem(this.item = item);
    }

    /**
     * Creates new armor stand block-item by parameters specified.
     *
     * @param uuid unique ID of the created entity
     * @param concurrent whether created block-item supports concurrent modification of players related to it
     * @param global whether created block-item is global (the value returned by {@link #isGlobal()})
     * @param viewDistance view distance of created block-item
     * @param visible whether created block-item should be visible
     * @param location location of created block-item
     * @param rotation rotation of created block item
     * @param small whether created block-item is small
     * @param marker whether created block-item is marker
     * @param item item to be displayed by this block-item
     * @return newly created armor stand block-item
     */
    public static ArmorStandBlockItem create(final @Nullable UUID uuid,
                                             final boolean concurrent,
                                             final boolean global, final int viewDistance, final boolean visible,
                                             final @Own @NonNull Location location,
                                             final @Own @NonNull Vector3F rotation,
                                             final boolean small, final boolean marker, final @NonNull ItemStack item) {
        final double itemCenterYOffset;
        final Offset offset;
        (offset = rotationOffsets(
                rotation, itemCenterYOffset = small ? ITEM_CENTER_Y_OFFSET * 0x1p-1 : ITEM_CENTER_Y_OFFSET)
        ).applyTo(location);

        return new ArmorStandBlockItem(
                uuid, concurrent ? new ConcurrentHashMap<>() : new HashMap<>(),
                global, viewDistance, visible,
                location.add(0, -(small ? ARMOR_STAND_HEAD_ROOT_OFFSET / 2 : ARMOR_STAND_HEAD_ROOT_OFFSET), 0),
                rotation, itemCenterYOffset, offset, small, marker, item
        );
    }

    @Override
    public @NonNull Location getLocation() {
        final Location location;
        offset.applyTo(location = super.getLocation());

        return location;
    }

    protected static @NotNull Offset rotationOffsets(final Vector3F rotation, double yOffset /* => y */) {
        // apply rotation matrices to align center: https://en.wikipedia.org/wiki/Rotation_matrix
        // let L be initial location and Q be geometrical center
        // the resulting location should be L' = L - Q'
        // where Q' = Mx(xRotation) * My(yRotation) * Mz(zRotation) * Q
        // and Mx, My and Mz are rotation matrices for the axes X, Y and Z respectively

        // for non-optimized implementation see commit 58899ac9450afb1e11e4a3b1ab923c139f4c7a29

        double angle;
        val z = -yOffset * sin(angle = toRadians(rotation.getX()));
        // minuses are used as we need to go to center instead of going from it
        return SimpleOffset.create(
                (yOffset *= cos(angle)) * sin(angle = toRadians(rotation.getZ())), -yOffset * cos(angle), z
        );
    }

    /**
     * Creates valid metadata for armor stand block-item.
     *
     * @param rotation rotation of this block-item
     * @param small whether this block-item is small
     * @param marker whether this block-item is marker
     * @return created metadata object
     */
    protected static WrappedDataWatcher createMetadata(final @Nullable Vector3F rotation,
                                                       final boolean small, final boolean marker) {
        val metadata = new ArrayList<WrappedWatchableObject>();

        metadata.add(air(300));
        metadata.add(noGravity(true));
        if (marker) {
            metadata.add(entityFlags(EntityFlag.INVISIBLE, EntityFlag.ON_FIRE));
            metadata.add(armorStandFlags(small ? new ArmorStandFlag[]{
                    SMALL, NO_BASE_PLATE, MARKER
            } : new ArmorStandFlag[]{
                    MARKER, NO_BASE_PLATE
            }));
        } else {
            metadata.add(entityFlags(EntityFlag.INVISIBLE));
            metadata.add(armorStandFlags(small
                    ? new ArmorStandFlag[]{SMALL, NO_BASE_PLATE}
                    : new ArmorStandFlag[]{NO_BASE_PLATE}
            ));
        }
        if (rotation != null) metadata.add(headRotation(rotation));

        return new WrappedDataWatcher(metadata);
    }

    @Override
    protected void performSpawnNoChecks(final @NotNull Player player) {
        super.performSpawnNoChecks(player);
        equipmentPacket.sendPacket(player);
    }

    /**
     * Sets this blocks rotation to the one specified.
     *
     * @param rotation new rotation of this block
     */
    protected void setRotationNoChecks(final @Own @NotNull Vector3F rotation) {
        { // overwrite the head's offset
            final Offset oldOffset = offset, newOffset;
            move(
                    (newOffset = offset = rotationOffsets(rotation, itemCenterYOffset)).x() - oldOffset.x(),
                    newOffset.y() - oldOffset.y(),
                    newOffset.z() - oldOffset.z()
            );
        }
        // overwrite the head's rotation
        addMetadata(headRotation(rotation));
    }

    /**
     * Rotates this block by specified delta. This means that its current
     * roll (<i>x</i>), pitch (<i>y</i>) and yaw (<i>z</i>) will each get incremented by those of delta specified.
     *
     * @param delta delta of rotation
     */
    public void rotate(final @NonNull @Own Vector3F delta) {
        final float dx, dy = delta.getY(), dz = delta.getZ();
        if (((dx = delta.getX()) == 0) && dy == 0 && dz == 0) return; // no-op

        final Vector3F thisRotation;
        if (((thisRotation = rotation) != null)) {
            delta.setX(thisRotation.getX() + dx);
            delta.setY(thisRotation.getY() + dy);
            delta.setZ(thisRotation.getZ() + dz);
        }
        setRotationNoChecks(delta);
    }

    /**
     * Sets this blocks rotation to the one specified.
     *
     * @param newRotation new rotation of this block
     */
    public void setRotation(final @Own @NonNull Vector3F newRotation) {
        if (!newRotation.equals(rotation)) setRotationNoChecks(newRotation);
    }

    public void setItem(final @Own @NonNull ItemStack item) {
        equipmentPacket.setItem(this.item = item);
        for (val entry : players.entrySet()) if (entry.getValue()) equipmentPacket.sendPacket(entry.getKey());
    }

    protected interface Offset {
        double x();

        double y();

        double z();

        void applyTo(@NotNull Location location);
    }

    @Value
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    protected static class SimpleOffset implements Offset {
        double x, y, z;

        @Override
        public void applyTo(final @NotNull Location location) {
            location.add(x, y, z);
        }

        public static @NotNull
        Offset create(final double x, final double y, final double z) {
            return new SimpleOffset(x, y, z);
        }
    }
}
