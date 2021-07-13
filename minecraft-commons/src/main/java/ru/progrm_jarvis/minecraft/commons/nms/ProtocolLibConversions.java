package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.Vector3F;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for conversions between <i>NSM</i> and <i>Protocol Lib</i> conversions.
 */
@UtilityClass
public class ProtocolLibConversions {

    @Contract("null -> null; !null -> !null")
    public @Nullable Object toNms(final @Nullable Vector3F vector3F) {
        return vector3F == null ? null : Vector3FConverterHolder.INSTANCE.getGeneric(vector3F);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable Vector3F toVector3F(final @Nullable Object nms) {
        return nms == null ? null : Vector3FConverterHolder.INSTANCE.getSpecific(nms);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable Object toNms(final @Nullable Direction direction) {
        return direction == null ? null : DirectionConverterHolder.INSTANCE.getGeneric(direction);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable Direction toDirection(final @Nullable Object nms) {
        return nms == null ? null : DirectionConverterHolder.INSTANCE.getSpecific(nms);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable Object toNms(final @Nullable Particle particle) {
        return particle == null ? null : ParticleConverterHolder.INSTANCE.getGeneric(particle);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable BlockPosition toBlockPosition(final @Nullable Object nms) {
        return nms == null ? null : BlockPositionConverterHolder.INSTANCE.getSpecific(nms);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable Object toNms(final @Nullable BlockPosition blockPosition) {
        return blockPosition == null ? null : BlockPositionConverterHolder.INSTANCE.getGeneric(blockPosition);
    }

    @Contract("null -> null; !null -> !null")
    public @Nullable Particle toParticle(final @Nullable Object nms) {
        return nms == null ? null : ParticleConverterHolder.INSTANCE.getSpecific(nms);
    }

    @UtilityClass
    private final class Vector3FConverterHolder {
        private final @NotNull EquivalentConverter<@NotNull Vector3F> INSTANCE = Vector3F.getConverter();
    }

    @UtilityClass
    private final class DirectionConverterHolder {
        private final @NotNull EquivalentConverter<@NotNull Direction> INSTANCE = EnumWrappers.getDirectionConverter();
    }

    @UtilityClass
    private final class ParticleConverterHolder {
        private final @NotNull EquivalentConverter<@NotNull Particle> INSTANCE = EnumWrappers.getParticleConverter();
    }

    @UtilityClass
    private final class BlockPositionConverterHolder {
        private final @NotNull EquivalentConverter<@NotNull BlockPosition> INSTANCE = BlockPosition.getConverter();
    }
}
