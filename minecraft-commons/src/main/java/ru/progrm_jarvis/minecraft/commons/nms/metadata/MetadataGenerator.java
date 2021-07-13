package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.nms.NmsUtil;

import java.util.UUID;

/**
 * <p>Editor for Metadata of {@link WrappedWatchableObject} providing classes
 * containing static methods for developer-friendly object creation.</p>
 *
 * <p>Version specifications:</p>
 * <ul>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=16539">1.16</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=15885">1.15</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=15063">1.14</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=14800">1.13</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=14048">1.12</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=8534">1.11</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=8241">1.10</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=7955">1.9</a></li>
 * <li><a href="https://wiki.vg/index.php?title=Entity_metadata&oldid=6816">1.8</a></li><!-- May be wrong -->
 * </ul>
 */
@UtilityClass
@SuppressWarnings({
        "ClassWithOnlyPrivateConstructors", // "sealed" classes
        "unused", // no way to correctly test methods
        "EmptyClass", "NonFinalUtilityClass", // ierarchy of entities
        "TypeMayBeWeakened" // enums implementing local interface
})
public class MetadataGenerator {

    private final int VERSION = NmsUtil.getVersion().getGeneration();
    private final @NonNull DataWatcherFactory FACTORY = NmsUtil.getDataWatcherFactory();

    private static void requireAtLeast(final int minVersion) {
        if (VERSION < minVersion) throw new UnsupportedOperationException(
                "This is not supported on versions prior to 1." + minVersion
        );
    }

    private static void requireAtMost(final int maxVersion) {
        if (VERSION > maxVersion) throw new UnsupportedOperationException(
                "This is not supported on versions after 1." + maxVersion
        );
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Entity {

        public static @NotNull WrappedWatchableObject entityFlags(final @NotNull EntityFlag @NonNull ...flags) {
            return FACTORY.createWatchable(0, ByteFlag.allChecked(flags));
        }

        public static @NotNull WrappedWatchableObject air(final int air) {
            return VERSION >= 9 ? FACTORY.createWatchable(1, air) : FACTORY.createWatchable(1, (short) air);
        }

        public static @NotNull WrappedWatchableObject air(final short air) {
            return VERSION >= 9 ? FACTORY.createWatchable(1, (int) air) : FACTORY.createWatchable(1, air);
        }

        public static @NotNull WrappedWatchableObject name(final @Nullable WrappedChatComponent name) {
            requireAtLeast(9);

            return VERSION >= 13
                    ? FACTORY.createWatchableOptional(2, name)
                    : FACTORY.createWatchable(2, name == null ? "" : name.getJson());
        }

        public static @NotNull WrappedWatchableObject name(final @Nullable String name) {
            requireAtLeast(9);

            return VERSION >= 13
                    ? FACTORY.createWatchableOptional(2, name == null ? null : WrappedChatComponent.fromText(name))
                    : FACTORY.createWatchable(2, name == null ? "" : name);
        }

        public static @NotNull WrappedWatchableObject nameVisible(final boolean nameVisible) {
            requireAtLeast(9);

            return FACTORY.createWatchable(3, nameVisible);
        }

        public static @NotNull WrappedWatchableObject silent(final boolean silent) {
            return FACTORY.createWatchable(4, silent);
        }

        public static @NotNull WrappedWatchableObject noGravity(final boolean noGravity) {
            requireAtLeast(10);

            return FACTORY.createWatchable(5, noGravity);
        }

        public static @NotNull WrappedWatchableObject pose(final EnumWrappers.EntityPose pose) {
            requireAtLeast(14);

            return FACTORY.createWatchable(6, pose);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum EntityFlag implements ByteFlag {
            ON_FIRE((byte) 0x01),
            CROUCHING((byte) 0x02),
            RIDING(VERSION >= 9 ? UNSUPPORTED : (byte) 0x04),
            SPRINTING((byte) 0x08),
            INTERACTING(VERSION >= 11 ? UNSUPPORTED : (byte) 0x10),
            SWIMMING(VERSION >= 11 ? (byte) 0x10 : UNSUPPORTED),
            INVISIBLE((byte) 0x20),
            GLOWING(VERSION >= 9 ? (byte) 0x40 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Projectile extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ItemedThrowable extends Projectile {} // note: there is no method as it's child-dependant

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Egg extends ItemedThrowable {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            requireAtLeast(14);

            return FACTORY.createWatchable(7, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EnderPearl extends ItemedThrowable {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            requireAtLeast(14);

            return FACTORY.createWatchable(7, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ExperienceBottle extends ItemedThrowable {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            requireAtLeast(14);

            return FACTORY.createWatchable(7, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Snowball extends ItemedThrowable {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            requireAtLeast(14);

            return FACTORY.createWatchable(7, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EyeOfEnder extends ItemedThrowable {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            requireAtLeast(14);

            return FACTORY.createWatchable(7, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Potion extends ItemedThrowable {

        public static @NotNull WrappedWatchableObject potion(final @NonNull ItemStack potion) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 14 ? 7 : VERSION >= 10 ? 6 : 5, potion);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FallingBlock extends Entity {

        public static @NotNull WrappedWatchableObject position(final @NonNull BlockPosition position) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5, position);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AreaEffectCloud extends Entity {

        public static @NotNull WrappedWatchableObject radius(final float radius) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5, radius);
        }

        public static @NotNull WrappedWatchableObject color(final int color) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 8 : VERSION >= 10 ? 7 : 6, color);
        }

        public static @NotNull WrappedWatchableObject singlePoint(final boolean singlePoint) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 9 : VERSION >= 10 ? 8 : 7, singlePoint);
        }

        public static @NotNull WrappedWatchableObject particle(final WrappedParticle<?> particle) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 10 : 9, particle);
        }

        public static @NotNull WrappedWatchableObject particle(final int particle) {
            requireAtLeast(10);
            requireAtMost(12);

            return FACTORY.createWatchable(9, particle);
        }

        public static @NotNull WrappedWatchableObject particleParameter1(final int particle) {
            requireAtLeast(11);
            requireAtMost(12);

            return FACTORY.createWatchable(10, particle);
        }

        public static @NotNull WrappedWatchableObject particleParameter2(final int particle) {
            requireAtLeast(11);
            requireAtMost(12);

            return FACTORY.createWatchable(11, particle);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FishingHook extends Entity {

        public static @NotNull WrappedWatchableObject hookedEntity(final int hookedEntityId) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5, hookedEntityId);
        }

        public static @NotNull WrappedWatchableObject hookedEntity(final @Nullable org.bukkit.entity.Entity entity) {
            requireAtLeast(9);

            return FACTORY.createWatchable(
                    VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5,
                    entity == null ? 0 : entity.getEntityId() + 1
            );
        }

        public static @NotNull WrappedWatchableObject catchable(final boolean catchable) {
            requireAtLeast(16);

            return FACTORY.createWatchable(8, catchable);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractArrow extends Entity {

        public static @NotNull WrappedWatchableObject arrowFlags(final @NotNull AbstractArrowFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : VERSION >= 9 ? 5 : 16, ByteFlag.allChecked(flags)
            );
        }

        public static @NotNull WrappedWatchableObject shooter(final @Nullable UUID shooterUuid) {
            requireAtLeast(13);
            requireAtLeast(15);

            return FACTORY.createWatchableOptional(VERSION >= 15 ? 8 : 7, shooterUuid);
        }

        public static @NotNull WrappedWatchableObject piercingLevel(final byte piercingLevel) {
            requireAtLeast(15);

            return FACTORY.createWatchable(VERSION >= 16 ? 8 : 9, piercingLevel);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum AbstractArrowFlag implements ByteFlag {
            CRITICAL((byte) 0x01),
            NO_CLIP(VERSION >= 13 ? (byte) 0x02 : (byte) -1);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Arrow extends AbstractArrow {

        public static @NotNull WrappedWatchableObject color(final int color) {
            requireAtLeast(16);

            return FACTORY.createWatchable(9, color);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TippedArrow extends AbstractArrow {

        public static @NotNull WrappedWatchableObject color(final int color) {
            requireAtLeast(9);

            return FACTORY.createWatchable(
                    VERSION >= 16 ? 9 /* Arrow */ : VERSION >= 15 ? 10 : VERSION >= 13 ? 8 : VERSION >= 10 ? 7 : 9,
                    color
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SpectralArrow extends AbstractArrow {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Trident extends AbstractArrow {

        public static @NotNull WrappedWatchableObject loyaltyLevel(final int loyaltyLevel) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 16 ? 9 : VERSION >= 15 ? 10 : 8, loyaltyLevel);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Boat extends Entity {

        public static @NotNull WrappedWatchableObject timeSinceLastHit(final int timeSinceLastHit) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : VERSION >= 9 ? 5 : 17,
                    timeSinceLastHit
            );
        }

        public static @NotNull WrappedWatchableObject forwardDirection(final int forwardDirection) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 8 : VERSION >= 10 ? 6 : VERSION >= 9 ? 6 : 18,
                    forwardDirection
            );
        }

        public static @NotNull WrappedWatchableObject damageTaken(final float damageTaken) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 9 : VERSION >= 10 ? 8 : VERSION >= 9 ? 7 : 19,
                    damageTaken
            );
        }

        public static @NotNull WrappedWatchableObject type(final @NonNull MetadataGenerator.Boat.BoatType type) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 10 : VERSION >= 10 ? 9 : 8, type.checkedValue());
        }

        public static @NotNull WrappedWatchableObject rightPaddleTurning(final boolean rightPaddleTurning) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 11 : VERSION >= 10 ? 10 : 9, rightPaddleTurning);
        }

        public static @NotNull WrappedWatchableObject leftPaddleTurning(final boolean leftPaddleTurning) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 12 : VERSION >= 10 ? 11 : 10, leftPaddleTurning);
        }

        public static @NotNull WrappedWatchableObject splashTimer(final int splashTimer) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 13 : 12, splashTimer);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum BoatType implements IntValue {
            OAK(VERSION >= 9 ? 0 : UNSUPPORTED),
            SPRUCE(VERSION >= 9 ? 1 : UNSUPPORTED),
            BIRCH(VERSION >= 9 ? 2 : UNSUPPORTED),
            JUNGLE(VERSION >= 9 ? 3 : UNSUPPORTED),
            ACACIA(VERSION >= 9 ? 4 : UNSUPPORTED),
            DARK_OAK(VERSION >= 9 ? 5 : UNSUPPORTED);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EnderCrystal extends Entity {

        public static @NotNull WrappedWatchableObject position(final @NonNull BlockPosition position) {
            requireAtLeast(9);

            return FACTORY.createWatchableOptional(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5, position);
        }

        public static @NotNull WrappedWatchableObject showBottom(final boolean showBottom) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 8 : VERSION >= 10 ? 7 : 6, showBottom);
        }

        public static @NotNull WrappedWatchableObject health(final int health) {
            requireAtLeast(9);
            requireAtMost(9);

            return FACTORY.createWatchable(8, health);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DragonFireball extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SmallFireball extends ItemedThrowable {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fireball extends ItemedThrowable {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WitherSkull extends Entity {

        public static @NotNull WrappedWatchableObject invulnerable(final boolean invulnerable) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5, invulnerable);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fireworks extends Entity {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : VERSION >= 9 ? 5 : 8, item);
        }

        public static @NotNull WrappedWatchableObject shooter(final @Nullable Integer shooter) {
            requireAtLeast(11);

            return VERSION >= 15
                    ? FACTORY.createWatchableOptional(8, shooter)
                    : FACTORY.createWatchable(7, shooter == null ? 0 : shooter);
        }

        public static @NotNull WrappedWatchableObject shooter(final @NonNull org.bukkit.entity.Entity entity) {
            requireAtLeast(11);

            val entityId = entity.getEntityId();
            return VERSION >= 15
                    ? FACTORY.createWatchableOptional(8, entityId)
                    : FACTORY.createWatchable(7, entityId);
        }

        public static @NotNull WrappedWatchableObject shotAtAngle(final boolean shotAtAngle) {
            requireAtLeast(9);

            return FACTORY.createWatchable(9, shotAtAngle);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Hanging extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ItemFrame extends Hanging {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : VERSION >= 9 ? 5 : 8, item);
        }

        public static @NotNull WrappedWatchableObject rotation(final int rotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 8 : VERSION >= 10 ? 7 : VERSION >= 9 ? 6 : 9, rotation);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Item extends Entity {

        public static @NotNull WrappedWatchableObject item(final @NonNull ItemStack item) {
            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : VERSION >= 9 ? 5 : 10, item);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LivingEntity extends Entity {

        public static @NotNull WrappedWatchableObject handStates(final @NotNull HandState @NonNull ...handStates) {
            requireAtLeast(9);

            return FACTORY.createWatchableObject(
                    VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5,
                    ByteFlag.allChecked(handStates)
            );
        }

        public static @NotNull WrappedWatchableObject health(final float health) {
            return FACTORY.createWatchable(VERSION >= 15 ? 8 : VERSION >= 10 ? 7 : 6, health);
        }

        public static @NotNull WrappedWatchableObject potionEffectColor(final int potionEffectColor) {
            return FACTORY.createWatchable(VERSION >= 15 ? 9 : VERSION >= 10 ? 8 : 7, potionEffectColor);
        }

        public static @NotNull WrappedWatchableObject potionEffectAmbient(final boolean potionEffectAmbient) {
            return FACTORY.createWatchable(VERSION >= 15 ? 9 : VERSION >= 10 ? 8 : 7, potionEffectAmbient);
        }

        public static @NotNull WrappedWatchableObject numberOfArrows(final int numberOfArrows) {
            return VERSION >= 9
                    ? FACTORY.createWatchable(VERSION >= 15 ? 11 : VERSION >= 10 ? 10 : 9, numberOfArrows)
                    : FACTORY.createWatchable(9, (byte) numberOfArrows);
        }

        public static @NotNull WrappedWatchableObject healthAddedByAbsorption(final int healthAddedByAbsorption) {
            requireAtLeast(15);

            return FACTORY.createWatchable(12, healthAddedByAbsorption);
        }

        public static @NotNull WrappedWatchableObject bedLocation(final @Nullable BlockPosition bedLocation) {
            requireAtLeast(14);

            return FACTORY.createWatchableOptional(VERSION >= 15 ? 13 : 12, bedLocation);
        }

        // since 1.9 this is part of Insentient
        public static @NotNull WrappedWatchableObject noAi(final boolean noAi) {
            requireAtMost(8);

            return FACTORY.createWatchable(15, noAi);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum HandState implements ByteFlag {
            HAND_ACTIVE(VERSION >= 9 ? (byte) 0x01 : UNSUPPORTED),
            OFFHAND(VERSION >= 9 ? (byte) 0x02 : UNSUPPORTED),
            RIPTIDE_SPIN_ATTACK(VERSION >= 13 ? (byte) 0x04 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Player extends LivingEntity {

        public static @NotNull WrappedWatchableObject additionalHearts(final float additionalHearts) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 14 : VERSION >= 10 ? 11 : VERSION >= 9 ? 10 : 17,
                    additionalHearts
            );
        }

        public static @NotNull WrappedWatchableObject score(final int score) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 18,
                    score
            );
        }

        public static @NotNull WrappedWatchableObject skinParts(final @NotNull SkinPart @NonNull ...skinParts) {
            return FACTORY.createWatchableObject(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 10,
                    ByteFlag.allChecked(skinParts)
            );
        }

        public static @NotNull WrappedWatchableObject hideCape(final boolean hideCape) {
            requireAtMost(8);

            return FACTORY.createWatchable(16, hideCape ? 0x02 : 0x00);
        }

        public static @NotNull WrappedWatchableObject mainHand(final @NonNull MainHand mainHand) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13, mainHand.checkedValue());
        }

        public static @NotNull WrappedWatchableObject leftShoulderEntity(
                final @NonNull NbtCompound leftShoulderEntity
        ) {
            requireAtLeast(12);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 15, leftShoulderEntity);
        }

        public static @NotNull WrappedWatchableObject rightShoulderEntity(
                final @NonNull NbtCompound rightShoulderEntity
        ) {
            requireAtLeast(12);

            return FACTORY.createWatchable(VERSION >= 15 ? 19 : 16, rightShoulderEntity);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum SkinPart implements ByteFlag {
            CAPE((byte) 0x01),
            JACKET((byte) 0x02),
            LEFT_SLEEVE((byte) 0x04),
            RIGHT_SLEEVE((byte) 0x08),
            LEFT_PANT((byte) 0x10),
            RIGHT_PANT((byte) 0x20),
            HAT((byte) 0x40);

            @Getter byte value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum MainHand implements ByteValue {
            LEFT(VERSION >= 9 ? (byte) 0 : UNSUPPORTED),
            RIGHT(VERSION >= 9 ? (byte) 1 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArmorStand extends LivingEntity {

        public static @NotNull WrappedWatchableObject armorStandFlags(final @NotNull ArmorStandFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 14 : VERSION >= 10 ? 11 : 10,
                    ByteFlag.allChecked(flags)
            );
        }

        public static @NotNull WrappedWatchableObject headRotation(final @NonNull Vector3F headRotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : 11, headRotation);
        }

        public static @NotNull WrappedWatchableObject bodyRotation(final @NonNull Vector3F bodyRotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : 12, bodyRotation);
        }

        public static @NotNull WrappedWatchableObject leftArmRotation(final @NonNull Vector3F leftArmRotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13, leftArmRotation);
        }

        public static @NotNull WrappedWatchableObject rightArmRotation(final @NonNull Vector3F rightArmRotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 18 : VERSION >= 10 ? 15 : 14, rightArmRotation);
        }

        public static @NotNull WrappedWatchableObject leftLegRotation(final @NonNull Vector3F leftLegRotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 19 : VERSION >= 10 ? 16 : 15, leftLegRotation);
        }

        public static @NotNull WrappedWatchableObject rightLegRotation(final @NonNull Vector3F rightLegRotation) {
            return FACTORY.createWatchable(VERSION >= 15 ? 20 : VERSION >= 10 ? 17 : 16, rightLegRotation);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum ArmorStandFlag implements ByteFlag {
            SMALL((byte) 0x01),
            HAS_GRAVITY(VERSION >= 10 ? UNSUPPORTED : (byte) 0x02),
            HAS_ARMS((byte) 0x04),
            NO_BASE_PLATE((byte) 0x08),
            MARKER((byte) 0x10);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Mob extends LivingEntity {

        public static @NotNull WrappedWatchableObject mobFlags(final @NotNull MobFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 14 : VERSION >= 10 ? 11 : VERSION >= 9 ? 10 : 15,
                    ByteFlag.allChecked(flags)
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum MobFlag implements ByteFlag {
            NO_AI((byte) 0x01),
            LEFT_HANDED(VERSION >= 9 ? (byte) 0x02 : UNSUPPORTED),
            AGGRESSIVE(VERSION >= 16 ? (byte) 0x04 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AmbientCreature extends Mob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Bat extends AmbientCreature {

        public static @NotNull WrappedWatchableObject batFlags(final @NotNull Flag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                    ByteFlag.allChecked(flags)
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Flag implements ByteFlag {
            HANGING((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PathfinderMob extends Mob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WaterAnimal extends PathfinderMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Squid extends WaterAnimal {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Dolphin extends WaterAnimal {

        public static @NotNull WrappedWatchableObject treasurePosition(final @NonNull BlockPosition treasurePosition) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : 12, treasurePosition);
        }

        public static @NotNull WrappedWatchableObject canFindTreasure(final boolean canFindTreasure) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 13, canFindTreasure);
        }

        public static @NotNull WrappedWatchableObject hasFish(final boolean hasFish) {
            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 14, hasFish);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractFish extends WaterAnimal {

        public static @NotNull WrappedWatchableObject fromBucket(final boolean fromBucket) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : 12, fromBucket);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cod extends AbstractFish {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PufferFish extends AbstractFish {

        public static @NotNull WrappedWatchableObject puffState(final int puffState) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 13, puffState);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Salmon extends AbstractFish {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TropicalFish extends AbstractFish {

        public static @NotNull WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 13, variant);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AgeableMob extends PathfinderMob {

        public static @NotNull WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : 12, baby);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Animal extends AgeableMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractHorse extends Animal {

        public static @NotNull WrappedWatchableObject horseFlags(final @NotNull HorseFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 16,
                    ByteFlag.allChecked(flags)
            );
        }

        public static @NotNull WrappedWatchableObject owner(final @Nullable UUID ownerUuid) {
            return VERSION >= 9 ? FACTORY.createWatchableOptional(
                    VERSION >= 15 ? 17 : VERSION >= 11 ? 14 : VERSION >= 10 ? 16 : 15,
                    ownerUuid
            ) : FACTORY.createWatchable(21, ownerUuid == null ? "" : Bukkit.getOfflinePlayer(ownerUuid).getName());
        }

        public static @NotNull WrappedWatchableObject owner(final @Nullable OfflinePlayer owner) {
            return VERSION >= 9 ? FACTORY.createWatchableOptional(
                    VERSION >= 15 ? 17 : VERSION >= 11 ? 14 : VERSION >= 10 ? 16 : 15,
                    owner == null ? null : owner.getUniqueId()
            ) : FACTORY.createWatchable(21, owner == null ? "" : owner.getName());
        }

        public static @NotNull WrappedWatchableObject owner(final @Nullable String ownerName) {
            return VERSION >= 9 ? FACTORY.createWatchableOptional(
                    VERSION >= 15 ? 17 : VERSION >= 11 ? 14 : VERSION >= 10 ? 16 : 15,
                    uuidByName(ownerName)
            ) : FACTORY.createWatchable(21, ownerName == null ? "" : ownerName);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum HorseFlag implements ByteFlag {
            TAME((byte) 0x02),
            SADDLED((byte) 0x04),
            CHEST(VERSION >= 12 ? UNSUPPORTED : (byte) 0x08), // legacy
            BRED(VERSION >= 12 ? (byte) 0x08 : (byte) 0x10),
            EATING(VERSION >= 12 ? (byte) 0x10 : (byte) 0x20),
            REARING(VERSION >= 12 ? (byte) 0x20 : (byte) 0x40),
            MOUTH_OPEN(VERSION >= 12 ? (byte) 0x40 : (byte) 0x80);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Horse extends AbstractHorse {

        public static @NotNull WrappedWatchableObject horseType(final @NonNull HorseType horseType) {
            requireAtLeast(8);
            requireAtMost(10);

            val value = horseType.checkedValue();
            return VERSION >= 9
                    ? FACTORY.createWatchable(VERSION >= 10 ? 14 : 13, (int) value)
                    : FACTORY.createWatchable(19, value);
        }

        public static @NotNull WrappedWatchableObject variant(final int variant) {
            return FACTORY.createWatchable(
                    VERSION > 15 ? 18 : VERSION >= 14 ? 17 : VERSION >= 10 ? 15 : VERSION >= 9 ? 14 : 20,
                    variant
            );
        }

        public static @NotNull WrappedWatchableObject armor(final @NonNull HorseArmor armor) {
            requireAtMost(13);

            return FACTORY.createWatchable(
                    VERSION >= 12 ? 17 : VERSION >= 11 ? 16 : VERSION >= 10 ? 17 : VERSION >= 9 ? 16 : 22,
                    armor.checkedValue()
            );
        }


        public static @NotNull WrappedWatchableObject forgeArmor(final @NonNull ItemStack item) {
            requireAtLeast(13);
            requireAtMost(13);

            return FACTORY.createWatchable(17, item);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum HorseType implements ByteValue {
            HORSE(VERSION >= 11 ? UNSUPPORTED : (byte) 0),
            DONKEY(VERSION >= 11 ? UNSUPPORTED : (byte) 1),
            MULE(VERSION >= 11 ? UNSUPPORTED : (byte) 2),
            ZOMBIE(VERSION >= 11 ? UNSUPPORTED : (byte) 3),
            SKELETON(VERSION >= 11 ? UNSUPPORTED : (byte) 4);

            @Getter byte value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum HorseArmor implements IntValue {
            NONE(VERSION >= 14 ? UNSUPPORTED : 0),
            IRON(VERSION >= 14 ? UNSUPPORTED : 1),
            GOLD(VERSION >= 14 ? UNSUPPORTED : 2),
            DIAMOND(VERSION >= 14 ? UNSUPPORTED : 3);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ZombieHorse extends AbstractHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SkeletonHorse extends AbstractHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChestedHorse extends AbstractHorse {

        public static @NotNull WrappedWatchableObject chest(final boolean chest) {
            requireAtLeast(12);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 15, chest);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Donkey extends ChestedHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Llama extends ChestedHorse {

        public static @NotNull WrappedWatchableObject strength(final int strength) {
            requireAtLeast(11);

            return FACTORY.createWatchable(VERSION >= 15 ? 19 : 16, strength);
        }

        public static @NotNull WrappedWatchableObject carpetColor(final int carpetColor) {
            requireAtLeast(11);

            return FACTORY.createWatchable(VERSION >= 15 ? 20 : 17, carpetColor);
        }

        public static @NotNull WrappedWatchableObject variant(final @NonNull LlamaVariant variant) {
            requireAtLeast(11);

            return FACTORY.createWatchable(VERSION >= 15 ? 21 : 18, variant.checkedValue());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum LlamaVariant implements IntValue {
            CREAMY(0),
            WHITE(1),
            BROWN(2),
            GRAY(3);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TraderLama {} // yup

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Mule extends ChestedHorse {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Bee extends Animal {

        public static @NotNull WrappedWatchableObject beeFlags(final @NotNull BeeFlag @NonNull ...flags) {
            requireAtLeast(15);

            return FACTORY.createWatchable(16, ByteFlag.allChecked(flags));
        }

        public static @NotNull WrappedWatchableObject angryTime(final int angryTime) {
            requireAtLeast(15);

            return FACTORY.createWatchable(17, angryTime);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum BeeFlag implements ByteFlag {
            ANGRY(VERSION >= 15 ? (byte) 0x02 : UNSUPPORTED),
            STUNG(VERSION >= 15 ? (byte) 0x04 : UNSUPPORTED),
            HAS_NECTAR(VERSION >= 15 ? (byte) 0x08 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Fox extends Animal {

        public static @NotNull WrappedWatchableObject foxType(final @NonNull FoxType type) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 15, type.checkedValue());
        }

        public static @NotNull WrappedWatchableObject foxFlags(final @NotNull FoxFlag @NonNull ...flags) {
            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 16, ByteFlag.allChecked(flags));
        }

        public static @NotNull WrappedWatchableObject firstUUID(final @Nullable UUID uuid) {
            return FACTORY.createWatchableOptional(VERSION >= 15 ? 18 : 17, uuid);
        }

        public static @NotNull WrappedWatchableObject secondUUID(final @Nullable UUID uuid) {
            return FACTORY.createWatchableOptional(VERSION >= 15 ? 19 : 18, uuid);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum FoxType implements IntValue {
            RED(0),
            SNOW(1);

            @Getter int value;
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum FoxFlag implements ByteFlag {
            SITTING((byte) 0x01),
            CROUCHING((byte) 0x04),
            INTERESTED((byte) 0x04),
            POUNCING((byte) 0x10),
            SLEEPING((byte) 0x20),
            FACEPLANTED((byte) 0x40),
            DEFENDING((byte) 0x80);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ocelot extends Animal {

        public static @NotNull WrappedWatchableObject trusting(final boolean trusting) {
            requireAtLeast(15);

            return FACTORY.createWatchable(16, trusting);
        }

        public static @NotNull WrappedWatchableObject variant(final @NonNull OcelotVariant variant) {
            requireAtMost(13);

            val value = variant.checkedValue();
            return VERSION >= 9
                    ? FACTORY.createWatchable(VERSION >= 10 ? 15 : 14, value)
                    : FACTORY.createWatchable(18, (byte) value);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum OcelotVariant implements IntValue {
            UNTAMED(VERSION >= 13 ? UNSUPPORTED : 0),
            TUXEDO(VERSION >= 13 ? UNSUPPORTED : 1),
            TABBY(VERSION >= 13 ? UNSUPPORTED : 2),
            SIAMESE(VERSION >= 13 ? UNSUPPORTED : 3);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Panda extends Animal {

        public static @NotNull WrappedWatchableObject breedTimer(final int breedTimer) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 15, breedTimer);
        }

        public static @NotNull WrappedWatchableObject sneezeTimer(final int sneezeTimer) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 16, sneezeTimer);
        }

        public static @NotNull WrappedWatchableObject earTimer(final int earTimer) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 17, earTimer);
        }

        public static @NotNull WrappedWatchableObject mainGene(final byte mainGene) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 19 : 18, mainGene);
        }

        public static @NotNull WrappedWatchableObject hiddenGene(final byte hiddenGene) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 20 : 19, hiddenGene);
        }

        public static @NotNull WrappedWatchableObject pandaFlags(final @NotNull PandaFlag @NonNull ...flags) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 21 : 20, ByteFlag.allChecked(flags));
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum PandaFlag implements ByteFlag {
            SNEEZING(VERSION >= 14 ? (byte) 0x02 : UNSUPPORTED),
            ROLLING(VERSION >= 14 ? (byte) 0x04 : UNSUPPORTED),
            SITTING(VERSION >= 14 ? (byte) 0x08 : UNSUPPORTED),
            ON_BACK(VERSION >= 14 ? (byte) 0x10 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pig extends Animal {

        public static @NotNull WrappedWatchableObject saddle(final boolean saddle) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 16, saddle);
        }

        public static @NotNull WrappedWatchableObject boostTime(final int boostTime) {
            requireAtLeast(11);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 14, boostTime);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Rabbit extends Animal {

        public static @NotNull WrappedWatchableObject type(final int type) {
            return VERSION >= 9
                    ? FACTORY.createWatchable(VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : 12, type)
                    : FACTORY.createWatchable(18, (byte) type);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Turtle extends Animal {

        public static @NotNull WrappedWatchableObject home(final @NonNull BlockPosition home) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 13, home);
        }

        public static @NotNull WrappedWatchableObject hasEgg(final boolean hasEgg) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 14, hasEgg);
        }

        public static @NotNull WrappedWatchableObject layingEgg(final boolean layingEgg) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 15, layingEgg);
        }

        public static @NotNull WrappedWatchableObject travelPosition(final @NonNull BlockPosition travelPosition) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 19 : 16, travelPosition);
        }

        public static @NotNull WrappedWatchableObject goingHome(final boolean goingHome) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 20 : 17, goingHome);
        }

        public static @NotNull WrappedWatchableObject travelling(final boolean travelling) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 21 : 18, travelling);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PolarBear extends Animal {

        public static @NotNull WrappedWatchableObject standingUp(final boolean standingUp) {
            requireAtLeast(10);

            return FACTORY.createWatchable(VERSION >= 15 ? 13 : 16, standingUp);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Chicken extends Animal {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cow extends Animal {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Hoglin extends Animal {

        public static @NotNull WrappedWatchableObject immuneToZombification(final boolean immuneToZombification) {
            requireAtLeast(16);

            return FACTORY.createWatchable(16, immuneToZombification);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Mooshroom extends Cow {

        public static @NotNull WrappedWatchableObject type(final @NonNull String type) {
            requireAtLeast(15);

            return FACTORY.createWatchable(16, type);
        }

        public static @NotNull WrappedWatchableObject type(final @NonNull MooshroomType type) {
            requireAtLeast(15);

            return FACTORY.createWatchable(16, type.checkedValue());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum MooshroomType implements StringValue {
            RED("red"),
            BROWN("brown");

            @Getter @NotNull String value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Sheep extends Animal {

        public static @NotNull WrappedWatchableObject sheepData(final @NonNull DyeColor color, final boolean sheared) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 16,
                    (byte) (woolId(color) | (sheared ? 0x10 : 0x00))
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Strider extends Animal {

        public static @NotNull WrappedWatchableObject boostTime(final int boostTime) {
            requireAtLeast(16);

            return FACTORY.createWatchable(16, boostTime);
        }

        public static @NotNull WrappedWatchableObject shaking(final boolean shaking) {
            requireAtLeast(16);

            return FACTORY.createWatchable(17, shaking);
        }

        public static @NotNull WrappedWatchableObject hasSaddle(final boolean hasSaddle) {
            requireAtLeast(16);

            return FACTORY.createWatchable(18, hasSaddle);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TameableAnimal extends Animal {

        public static @NotNull WrappedWatchableObject tameableAnimalFlags(
                final @NotNull TameableAnimalFlag @NonNull ...flags
        ) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 16,
                    ByteFlag.allChecked(flags)
            );
        }

        public static @NotNull WrappedWatchableObject owner(final @Nullable UUID ownerUuid) {
            return VERSION >= 9
                    ? FACTORY.createWatchableOptional(VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13, ownerUuid)
                    : FACTORY.createWatchable(
                            17, ownerUuid == null ? "" : Bukkit.getOfflinePlayer(ownerUuid).getName()
                    );
        }

        public static @NotNull WrappedWatchableObject owner(final @Nullable OfflinePlayer owner) {
            return VERSION >= 9 ? FACTORY.createWatchableOptional(
                    VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13,
                    owner == null ? null : owner.getUniqueId()
            ) : FACTORY.createWatchable(17, owner == null ? null : owner.getName());
        }

        public static @NotNull WrappedWatchableObject owner(final @Nullable String ownerName) {
            return VERSION >= 9 ? FACTORY.createWatchableOptional(
                    VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13,
                    uuidByName(ownerName)
            ) : FACTORY.createWatchable(21, ownerName == null ? "" : ownerName);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum TameableAnimalFlag implements ByteFlag {
            SITTING(VERSION >= 9 ? (byte) 0x01 : UNSUPPORTED),
            ANGRY(VERSION >= 16 ? UNSUPPORTED : VERSION >= 9 ? (byte) 0x02 : UNSUPPORTED),
            TAMED(VERSION >= 9 ? (byte) 0x04 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Cat extends TameableAnimal {

        public static @NotNull WrappedWatchableObject catVariant(final @NonNull CatVariant variant) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 17, variant.checkedValue());
        }

        public static @NotNull WrappedWatchableObject lying(final boolean lying) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 19 : 18, lying);
        }

        public static @NotNull WrappedWatchableObject relaxed(final boolean relaxed) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 20 : 19, relaxed);
        }

        public static @NotNull WrappedWatchableObject collarColor(final @NonNull DyeColor collarColor) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 21 : 20, (int) woolId(collarColor));
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum CatVariant implements IntValue {
            TABBY(0),
            BLACK(1),
            RED(2),
            SIAMESE(3),
            BRITISH_SHORTHAIR(4),
            CALICO(5),
            PERSIAN(6),
            RAGDOLL(7),
            WHITE(8),
            JELLIE(VERSION >= 16 ? 9 : UNSUPPORTED),
            ALL_BLACK(VERSION >= 16 ? 10 : 9);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Wolf extends TameableAnimal {

        public static @NotNull WrappedWatchableObject begging(final boolean begging) {
            return FACTORY.createWatchable(VERSION >= 16 ? 18 : VERSION >= 10 ? 16 : VERSION >= 9 ? 15 : 19, begging);
        }

        public static @NotNull WrappedWatchableObject collarColor(final @NonNull DyeColor collarColor) {
            val value = woolId(collarColor);
            return VERSION >= 9
                    ? FACTORY.createWatchable(VERSION >= 16 ? 19 : VERSION >= 10 ? 17 : 16, (int) value)
                    : FACTORY.createWatchable(20, value);
        }

        public static @NotNull WrappedWatchableObject damageTaken(final float damageTaken) {
            requireAtMost(14);

            return FACTORY.createWatchable(VERSION >= 10 ? 15 : VERSION >= 9 ? 14 : 18, damageTaken);
        }

        public static @NotNull WrappedWatchableObject angerTime(final int angerTime) {
            requireAtLeast(16);

            return FACTORY.createWatchable(20, angerTime);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Parrot extends TameableAnimal {

        public static @NotNull WrappedWatchableObject variant(final @NonNull Variant variant) {
            requireAtLeast(12);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 15, variant.checkedValue());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Variant implements IntValue {
            RED_BLUE(0),
            BLUE(1),
            GREEN(2),
            YELLOW_BLUE(3),
            GREY(4);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractVillager extends AgeableMob {

        public static @NotNull WrappedWatchableObject headShakeTimer(final int headShakeTimer) {
            requireAtLeast(16);

            return FACTORY.createWatchable(16, headShakeTimer);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Villager extends AbstractVillager {

        public static @NotNull WrappedWatchableObject profession(final @NonNull VillagerProfession profession) {
            requireAtMost(13);

            return FACTORY.createWatchable(VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 16, profession.checkedValue());
        }

        public static @NotNull WrappedWatchableObject villagerData(final @NonNull WrappedVillagerData villagerData) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 16, villagerData);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum VillagerProfession implements IntValue {
            FARMER(VERSION >= 14 ? UNSUPPORTED : 0),
            LIBRARIAN(VERSION >= 14 ? UNSUPPORTED : 1),
            PRIEST(VERSION >= 14 ? UNSUPPORTED : 2),
            BLACKSMITH(VERSION >= 14 ? UNSUPPORTED : 3),
            SILVER(VERSION >= 14 ? UNSUPPORTED : 4);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WanderingTrader extends AbstractVillager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractGolem extends PathfinderMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IronGolem extends AbstractGolem {

        public static @NotNull WrappedWatchableObject ironGolemFlags(final @NotNull IronGolemFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                    ByteFlag.allChecked(flags)
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum IronGolemFlag implements ByteFlag {
            PLAYER_CREATED((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SnowGolem extends AbstractGolem {

        public static @NotNull WrappedWatchableObject snowGolemFlags(final @NotNull SnowGolemFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 10 : 15 /* missing spec for this */,
                    ByteFlag.allChecked(flags)
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum SnowGolemFlag implements ByteFlag {
            PLAYER_CREATED((byte) 0x10);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Shulker extends AbstractGolem {

        public static @NotNull WrappedWatchableObject attachFace(final @NonNull EnumWrappers.Direction direction) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : 11, direction);
        }

        public static @NotNull WrappedWatchableObject attachmentPosition(
                final @Nullable BlockPosition attachmentPosition
        ) {
            requireAtLeast(9);

            return FACTORY.createWatchableOptional(VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : 12, attachmentPosition);
        }

        public static @NotNull WrappedWatchableObject shieldHeight(final byte shieldHeight) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13, shieldHeight);
        }

        public static @NotNull WrappedWatchableObject color(final @NonNull DyeColor dyeColor) {
            requireAtLeast(11);

            return FACTORY.createWatchable(VERSION >= 15 ? 18 : 15, woolId(dyeColor));
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Monster extends PathfinderMob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BasePiglin extends PathfinderMob {

        public static @NotNull WrappedWatchableObject immuneToZombification(final boolean immuneToZombification) {
            requireAtLeast(16);

            return FACTORY.createWatchable(15, immuneToZombification);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Piglin extends BasePiglin {

        public static @NotNull WrappedWatchableObject baby(final boolean baby) {
            requireAtLeast(16);

            return FACTORY.createWatchable(16, baby);
        }

        public static @NotNull WrappedWatchableObject chargingCrossbow(final boolean chargingCrossbow) {
            requireAtLeast(16);

            return FACTORY.createWatchable(17, chargingCrossbow);
        }

        public static @NotNull WrappedWatchableObject dancing(final boolean dancing) {
            requireAtLeast(16);

            return FACTORY.createWatchable(18, dancing);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PiglinBrute extends BasePiglin {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Blaze extends Monster {

        public static @NotNull WrappedWatchableObject blazeFlags(final @NotNull BlazeFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                    ByteFlag.allChecked(flags)
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum BlazeFlag implements ByteFlag {
            ON_FIRE((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Creeper extends Monster {

        public static @NotNull WrappedWatchableObject creeperState(final @NonNull CreeperState state) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                    state.checkedValue()
            );
        }

        public static @NotNull WrappedWatchableObject charged(final boolean charged) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 17, charged);
        }

        public static @NotNull WrappedWatchableObject ignited(final boolean ignited) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : 13, ignited);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum CreeperState implements IntValue {
            IDLE(-1),
            FUSE(1);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Endermite extends Monster {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Giant extends Monster {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Guardian extends Monster {

        public static @NotNull WrappedWatchableObject retractingSpikes(final boolean retractingSpikes) {
            return VERSION >= 11
                    ? FACTORY.createWatchable(VERSION >= 15 ? 15 : 12, retractingSpikes)
                    : FACTORY.createWatchable(
                            VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                            GuardianFlag.RETRACTING_SPIKES.checkedValue()
                    );
        }

        public static @NotNull WrappedWatchableObject guardianFlags(final @NonNull GuardianFlag... guardianFlags) {
            requireAtMost(10);

            return FACTORY.createWatchable(
                    VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                    ByteFlag.allChecked(guardianFlags)
            );
        }

        public static @NotNull WrappedWatchableObject targetEntity(final int targetEntityId) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 17,
                    targetEntityId
            );
        }

        public static @NotNull WrappedWatchableObject targetEntity(final @NonNull org.bukkit.entity.Entity entity) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 17,
                    entity.getEntityId()
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum GuardianFlag implements ByteFlag {
            ELDERY(VERSION >= 11 ? UNSUPPORTED : (byte) 0x02),
            RETRACTING_SPIKES((byte) 0x04);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ElderGuardian extends Guardian {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Silverfish extends Monster {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Raider extends Monster {

        public static @NotNull WrappedWatchableObject celebrating(final boolean celebrating) {
            requireAtLeast(14);

            return FACTORY.createWatchable(VERSION >= 15 ? 15 : 14, celebrating);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractIllager extends Raider {

        public static @NotNull WrappedWatchableObject abstractIllagerFlags(
                final @NotNull AbstractIllagerFlag @NonNull ...flags
        ) {
            requireAtLeast(12);
            requireAtMost(13);

            return FACTORY.createWatchable(12, ByteFlag.allChecked(flags));
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum AbstractIllagerFlag implements ByteFlag {
            HAS_TARGET(VERSION >= 14 ? UNSUPPORTED : (byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Vindicator extends AbstractIllager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Pillager extends AbstractIllager {

        public static @NotNull WrappedWatchableObject charging(final boolean charging) {
            requireAtLeast(16);

            // note: missing spec for 1.15
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : 15, charging);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SpellcasterIllager extends AbstractIllager {

        public static @NotNull WrappedWatchableObject spell(final @NonNull Spell spell) {
            return FACTORY.createWatchable(13, spell.checkedValue());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum Spell implements ByteValue {
            NONE(VERSION >= 12 ? (byte) 0 : UNSUPPORTED),
            SUMMON_VEX(VERSION >= 12 ? (byte) 1 : UNSUPPORTED),
            ATTACK(VERSION >= 12 ? (byte) 2 : UNSUPPORTED),
            WOLOLO(VERSION >= 12 ? (byte) 3 : UNSUPPORTED),
            DISAPPEAR(VERSION >= 13 ? (byte) 5 : UNSUPPORTED),
            BLINDNESS(VERSION >= 13 ? (byte) 6 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Evoker extends SpellcasterIllager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Illusioner extends SpellcasterIllager {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ravager extends Raider {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Witch extends Raider {

        public static @NotNull WrappedWatchableObject drinkingPotion(final boolean drinkingPotion) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 14 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 21,
                    drinkingPotion
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EvokerFangs extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Vex extends Monster {

        public static @NotNull WrappedWatchableObject vexFlags(final @NotNull VexFlag @NonNull ...flags) {
            requireAtLeast(11);

            return FACTORY.createWatchable(VERSION >= 15 ? 15 : 12, ByteFlag.allChecked(flags));
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum VexFlag implements ByteFlag {
            ATTACKING(VERSION >= 11 ? (byte) 0x01 : UNSUPPORTED);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractSkeleton extends Monster {

        public static @NotNull WrappedWatchableObject skeletonType(final @NonNull SkeletonType type) {
            requireAtMost(10);

            return FACTORY.createWatchable(VERSION >= 10 ? 13 : VERSION >= 9 ? 11 : 13, type.checkedValue());
        }

        public static @NotNull WrappedWatchableObject swingingArms(final boolean swingingArms) {
            requireAtLeast(9);
            requireAtMost(13);

            return FACTORY.createWatchable(VERSION >= 11 ? 12 : VERSION >= 10 ? 13 : 12, swingingArms);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum SkeletonType implements ByteValue {
            NORMAL(VERSION >= 11 ? UNSUPPORTED : (byte) 0),
            WITHER(VERSION >= 11 ? UNSUPPORTED : (byte) 1);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Skeleton extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WitherSkeleton extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Stray extends AbstractSkeleton {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Spider extends Monster {

        public static @NotNull WrappedWatchableObject spiderFlags(final @NotNull SpiderFlag @NonNull ...flags) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16,
                    ByteFlag.allChecked(flags)
            );
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @SuppressWarnings("Singleton") // there just is single entry in this enum
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum SpiderFlag implements ByteFlag {
            CLIMBING((byte) 0x01);

            @Getter byte value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Wither extends Monster {

        public static @NotNull WrappedWatchableObject centerHeadTarget(final int centerHeadTargetId) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 17,
                    centerHeadTargetId
            );
        }

        public static @NotNull WrappedWatchableObject centerHeadTarget(
                final @Nullable org.bukkit.entity.Entity centerHeadTarget
        ) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 17,
                    centerHeadTarget == null ? 0 : centerHeadTarget.getEntityId()
            );
        }

        public static @NotNull WrappedWatchableObject leftHeadTarget(final int leftHeadTargetId) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 18,
                    leftHeadTargetId
            );
        }

        public static @NotNull WrappedWatchableObject leftHeadTarget(
                final @NonNull org.bukkit.entity.Entity leftHeadTarget
        ) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : VERSION >= 9 ? 12 : 18,
                    leftHeadTarget.getEntityId()
            );
        }

        public static @NotNull WrappedWatchableObject rightHeadTarget(final int rightHeadTargetId) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : VERSION >= 9 ? 13 : 19,
                    rightHeadTargetId
            );
        }

        public static @NotNull WrappedWatchableObject rightHeadTarget(
                final @NonNull org.bukkit.entity.Entity rightHeadTarget
        ) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 17 : VERSION >= 10 ? 14 : VERSION >= 9 ? 13 : 19,
                    rightHeadTarget.getEntityId()
            );
        }

        public static @NotNull WrappedWatchableObject invulnerableTime(final int invulnerableTime) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 18 : VERSION >= 10 ? 15 : VERSION >= 9 ? 14 : 20,
                    invulnerableTime
            );
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Zoglin extends Monster {

        public static @NotNull WrappedWatchableObject baby(final boolean baby) {
            requireAtLeast(16);

            return FACTORY.createWatchable(15, baby);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Zombie extends Monster {

        public static @NotNull WrappedWatchableObject baby(final boolean baby) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 12, baby);
        }

        public static @NotNull WrappedWatchableObject zombieVillager(final boolean zombieVillager) {
            requireAtMost(8);

            return FACTORY.createWatchable(13, zombieVillager);
        }

        public static @NotNull WrappedWatchableObject zombieType(final int zombieType) {
            requireAtLeast(9);
            requireAtMost(10);

            return FACTORY.createWatchable(VERSION >= 10 ? 13 : 12, zombieType);
        }

        public static @NotNull WrappedWatchableObject converting(final boolean converting) {
            requireAtMost(10);

            return FACTORY.createWatchable(VERSION >= 10 ? 14 : VERSION >= 9 ? 13 : 14, converting);
        }

        public static @NotNull WrappedWatchableObject handsUp(final boolean handsUp) {
            requireAtLeast(9);
            requireAtMost(14);

            return FACTORY.createWatchable(VERSION >= 11 ? 14 : VERSION >= 10 ? 15 : 14, handsUp);
        }

        public static @NotNull WrappedWatchableObject becomingDrowned(final boolean becomingDrowned) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 17 : 15, becomingDrowned);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ZombieVillager extends Zombie {

        public static @NotNull WrappedWatchableObject converting(final boolean converting) {
            return FACTORY.createWatchable(
                    VERSION >= 15 ? 18 : VERSION >= 13 ? 16 : VERSION >= 11
                            ? 15 : VERSION >= 10 ? 14 : VERSION >= 9 ? 13 : 14,
                    converting
            );
        }

        public static @NotNull WrappedWatchableObject profession(final int profession) {
            requireAtLeast(11);
            requireAtMost(14);

            return FACTORY.createWatchable(VERSION >= 13 ? 17 : 16, profession);
        }

        public static @NotNull WrappedWatchableObject villagerData(final @NonNull WrappedVillagerData villagerData) {
            requireAtLeast(15);

            return FACTORY.createWatchable(19, villagerData);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Husk extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Drowned extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ZombifiedPiglin extends Zombie {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Enderman extends Monster {

        public static @NotNull WrappedWatchableObject carriedBlockId(final short carriedBlockId) {
            requireAtMost(8);

            return FACTORY.createWatchable(16, carriedBlockId);
        }

        public static @NotNull WrappedWatchableObject carriedBlockData(final byte carriedBlockData) {
            requireAtMost(8);

            return FACTORY.createWatchable(17, carriedBlockData);
        }

        public static @NotNull WrappedWatchableObject carriedBlock(final @Nullable WrappedBlockData carriedBlock) {
            requireAtLeast(9);

            return FACTORY.createWatchableOptional(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : 11, carriedBlock);
        }

        public static @NotNull WrappedWatchableObject screaming(final boolean screaming) {
            return FACTORY.createWatchable(VERSION >= 15 ? 16 : VERSION >= 10 ? 13 : 12, screaming);
        }

        public static @NotNull WrappedWatchableObject staring(final boolean staring) {
            requireAtLeast(15);

            return FACTORY.createWatchable(17, staring);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EnderDragon extends Mob {

        public static @NotNull WrappedWatchableObject phase(final @NonNull EnderDragonPhase phase) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : 11, phase.checkedValue());
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public enum EnderDragonPhase implements IntValue {
            CIRCLING(VERSION >= 9 ? 0 : UNSUPPORTED),
            STRAFING(VERSION >= 9 ? 1 : UNSUPPORTED),
            FLYING_TO_LAND(VERSION >= 9 ? 2 : UNSUPPORTED),
            LANDING(VERSION >= 9 ? 3 : UNSUPPORTED),
            TAKING_OFF(VERSION >= 9 ? 4 : UNSUPPORTED),
            BREATHING(VERSION >= 9 ? 5 : UNSUPPORTED),
            LOOKING_FOR_PLAYER(VERSION >= 9 ? 6 : UNSUPPORTED),
            ROARING(VERSION >= 9 ? 7 : UNSUPPORTED),
            CHARGING_PLAYER(VERSION >= 9 ? 8 : UNSUPPORTED),
            FLYING_TO_DIE(VERSION >= 9 ? 9 : UNSUPPORTED),
            NO_AI(VERSION >= 9 ? 10 : UNSUPPORTED);

            @Getter int value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Flying extends Mob {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ghast extends Flying {

        public static @NotNull WrappedWatchableObject attacking(final boolean attacking) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 11 : 16, attacking);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Phantom extends Flying {

        public static @NotNull WrappedWatchableObject size(final int size) {
            requireAtLeast(13);

            return FACTORY.createWatchable(VERSION >= 15 ? 15 : 12, size);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Slime extends Mob {

        public static @NotNull WrappedWatchableObject size(final int size) {
            return FACTORY.createWatchable(VERSION >= 15 ? 15 : VERSION >= 10 ? 12 : VERSION >= 9 ? 12 : 16, size);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LlamaSpit extends Entity {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractMinecart extends Entity {

        private static final int OFFSET = VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : VERSION >= 9 ? 5 : 17;
        private static final int MAX_INDEX = OFFSET + 5;

        public static @NotNull WrappedWatchableObject shakingPower(final int shakingPower) {
            return FACTORY.createWatchable(OFFSET, shakingPower);
        }

        public static @NotNull WrappedWatchableObject shakingDirection(final int shakingDirection) {
            return FACTORY.createWatchable(OFFSET + 1, shakingDirection);
        }

        public static @NotNull WrappedWatchableObject shakingMultiplier(final int shakingMultiplier) {
            return FACTORY.createWatchable(OFFSET + 2, shakingMultiplier);
        }

        public static @NotNull WrappedWatchableObject customBlockIdAndDamage(final int customBlockIdAndDamage) {
            return FACTORY.createWatchable(OFFSET + 3, customBlockIdAndDamage);
        }

        public static @NotNull WrappedWatchableObject customBlockY(final int customBlockY) {
            return FACTORY.createWatchable(OFFSET + 4, customBlockY);
        }

        public static @NotNull WrappedWatchableObject showCustomBlock(final boolean showCustomBlock) {
            return FACTORY.createWatchable(OFFSET + 5, showCustomBlock);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Minecart extends AbstractMinecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AbstractMinecartContainer extends AbstractMinecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartHopper extends AbstractMinecartContainer {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartChest extends AbstractMinecartContainer {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartFurnace extends AbstractMinecart {

        private static final int OFFSET = AbstractMinecart.MAX_INDEX + 1;
        private static final int MAX_INDEX = OFFSET;

        public static @NotNull WrappedWatchableObject powered(final boolean powered) {
            return FACTORY.createWatchable(MAX_INDEX, powered);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartTnt extends AbstractMinecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartSpawner extends AbstractMinecart {}

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinecartCommandBlock extends AbstractMinecart {

        private static final int OFFSET = AbstractMinecart.MAX_INDEX + 1;
        private static final int MAX_INDEX = OFFSET + 1;

        public static @NotNull WrappedWatchableObject command(final @NonNull String command) {
            requireAtLeast(9);

            return FACTORY.createWatchable(OFFSET, command);
        }

        public static @NotNull WrappedWatchableObject lastOutput(final @NonNull WrappedChatComponent lastOutput) {
            requireAtLeast(9);

            return FACTORY.createWatchable(MAX_INDEX, lastOutput);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TNTPrimed extends Entity {

        public static @NotNull WrappedWatchableObject fuseTime(final int fuseTime) {
            requireAtLeast(9);

            return FACTORY.createWatchable(VERSION >= 15 ? 7 : VERSION >= 10 ? 6 : 5, fuseTime);
        }
    }

    @SuppressWarnings("deprecation")
    private static byte woolId(final @NotNull DyeColor color) {
        return color.getWoolData();
    }

    @SuppressWarnings("deprecation")
    @Contract("null -> null")
    private static @Nullable OfflinePlayer offlinePlayerByName(final @Nullable String name) {
        return name == null ? null : Bukkit.getOfflinePlayer(name);
    }

    @Contract("null -> null")
    private static @Nullable UUID uuidByName(final @Nullable String name) {
        final OfflinePlayer player;
        return (player = offlinePlayerByName(name)) == null ? null : player.getUniqueId();
    }

    @UtilityClass
    private static final @NonNull class EmptyChatComponent {
        private final @NotNull WrappedChatComponent INSTANCE = WrappedChatComponent.fromJson("{\"text\":\"\"}");
    }

    @FunctionalInterface
    private interface ByteValue {
        byte UNSUPPORTED = -1;

        byte value();

        default byte checkedValue() {
            final byte value;
            if ((value = value()) == UNSUPPORTED) throw new UnsupportedOperationException(
                    "Value " + this + " is not supported on version 1." + VERSION
            );

            return value;
        }
    }

    @FunctionalInterface
    private interface IntValue {
        int UNSUPPORTED = -1;

        int value();

        default int checkedValue() {
            final int value;
            if ((value = value()) == UNSUPPORTED) throw new UnsupportedOperationException(
                    "Value " + this + " is not supported on version 1." + VERSION
            );

            return value;
        }
    }

    @FunctionalInterface
    private interface StringValue {
        String UNSUPPORTED = null;

        @Nullable String value();

        default @NotNull String checkedValue() {
            final String value;
            if ((value = value()) == null /* == UNSUPPORTED */) throw new UnsupportedOperationException(
                    "Value " + this + " is not supported on version 1." + VERSION
            );

            return value;
        }
    }

    @FunctionalInterface
    private interface ByteFlag extends ByteValue {

        // No need for explicit null-check as the interface is private
        static byte allChecked(final @NotNull ByteFlag @NotNull ... flags) {
            byte value = 0;
            for (val flag : flags) value |= flag.value();

            return value;
        }
    }
}
