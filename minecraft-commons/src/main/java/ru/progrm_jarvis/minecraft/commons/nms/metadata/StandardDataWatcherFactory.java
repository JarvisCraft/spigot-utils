package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * DataWatcher factory for post 1.9 versions.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StandardDataWatcherFactory implements DataWatcherFactory {

    private static final @NotNull WrappedDataWatcher.Serializer BYTE_SERIALIZER
            = Registry.get(Byte.class);
    private static final @NotNull WrappedDataWatcher.Serializer INT_SERIALIZER
            = Registry.get(Integer.class);
    private static final @NotNull WrappedDataWatcher.Serializer FLOAT_SERIALIZER
            = Registry.get(Float.class);
    private static final @NotNull WrappedDataWatcher.Serializer STRING_SERIALIZER
            = Registry.get(String.class);
    private static final @NotNull WrappedDataWatcher.Serializer I_CHAT_BASE_COMPONENT_SERIALIZER
            = Registry.getChatComponentSerializer();
    private static final @NotNull WrappedDataWatcher.Serializer ITEM_STACK_SERIALIZER
            = Registry.getItemStackSerializer(false);
    private static final @NotNull WrappedDataWatcher.Serializer OPTIONAL_I_BLOCK_DATA_SERIALIZER
            = Registry.getBlockDataSerializer(true);
    private static final @NotNull WrappedDataWatcher.Serializer BOOLEAN_SERIALIZER
            = Registry.get(Boolean.class);
    private static final @NotNull WrappedDataWatcher.Serializer VECTOR_3F_SERIALIZER
            = Registry.getVectorSerializer();
    private static final @NotNull WrappedDataWatcher.Serializer BLOCK_POSITION_SERIALIZER
            = Registry.getBlockPositionSerializer(false);
    private static final @NotNull WrappedDataWatcher.Serializer OPTIONAL_BLOCK_POSITION_SERIALIZER
            = Registry.getBlockPositionSerializer(true);
    private static final @NotNull WrappedDataWatcher.Serializer ENUM_DIRECTION_SERIALIZER
            = Registry.getDirectionSerializer();
    private static final @NotNull WrappedDataWatcher.Serializer OPTIONAL_UUID_SERIALIZER
            = Registry.getUUIDSerializer(true);
    private static final @NotNull WrappedDataWatcher.Serializer NBT_TAG_COMPOUND_SERIALIZER
            = Registry.getNBTCompoundSerializer();
    // Previously unsupported types
    private static final @Nullable WrappedDataWatcher.Serializer OPTIONAL_I_CHAT_BASE_COMPONENT_SERIALIZER;
    private static final @Nullable WrappedDataWatcher.Serializer PARTICLE_SERIALIZER;
    private static final @Nullable WrappedDataWatcher.Serializer VILLAGER_DATA_SERIALIZER;
    private static final @Nullable WrappedDataWatcher.Serializer ENTITY_POSE_SERIALIZER;
    private static final @Nullable WrappedDataWatcher.Serializer OPTIONAL_INT_SERIALIZER;

    // Unsupported types
    private static final @Nullable WrappedDataWatcher.Serializer SHORT_SERIALIZER;

    static {
        WrappedDataWatcher.Serializer serializer;

        try {
            serializer = Registry.get(Integer.class, true);
        } catch (final IllegalArgumentException e) {
            serializer = null;
        }
        OPTIONAL_INT_SERIALIZER = serializer;

        try {
            serializer = Registry.getChatComponentSerializer(true);
        } catch (final IllegalArgumentException e) {
            serializer = null;
        }
        OPTIONAL_I_CHAT_BASE_COMPONENT_SERIALIZER = serializer;

        {
            final Class<?> nmsClass;
            if ((nmsClass = MinecraftReflection.getParticleTypeClass()) != null) try {
                serializer = Registry.get(nmsClass);
            } catch (final IllegalArgumentException e) {
                serializer = null;
            } else serializer = null;
        }
        PARTICLE_SERIALIZER = serializer;

        {
            final Class<?> nmsClass;
            if ((nmsClass = WrappedVillagerData.getNmsClass()) != null) try {
                serializer = Registry.get(nmsClass);
            } catch (final IllegalArgumentException e) {
                serializer = null;
            } else serializer = null;
        }
        VILLAGER_DATA_SERIALIZER = serializer;

        {
            final Class<?> nmsClass;
            if ((nmsClass = EnumWrappers.getEntityPoseClass()) != null) try {
                serializer = Registry.get(nmsClass);
            } catch (final IllegalArgumentException e) {
                serializer = null;
            } else serializer = null;
        }
        ENTITY_POSE_SERIALIZER = serializer;

        try {
            serializer = Registry.get(Short.class);
        } catch (final IllegalArgumentException e) {
            serializer = null;
        }
        SHORT_SERIALIZER = serializer;
    }

    public static @NotNull DataWatcherFactory create() {
        return new StandardDataWatcherFactory();
    }

    @Override
    public @NotNull DataWatcherModifier modifier(WrappedDataWatcher watcher) {
        return new StandardDataWatcherModifier(new WrappedDataWatcher());
    }

    @Override
    public @NotNull DataWatcherModifier modifier() {
        return new StandardDataWatcherModifier(new WrappedDataWatcher());
    }

    // Actual types

    private @NotNull WrappedDataWatcherObject watcherObjectByte(final int id) {
        return new WrappedDataWatcherObject(id, BYTE_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectInt(final int id) {
        return new WrappedDataWatcherObject(id, INT_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectFloat(final int id) {
        return new WrappedDataWatcherObject(id, FLOAT_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectString(final int id) {
        return new WrappedDataWatcherObject(id, STRING_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectIChatBaseComponent(final int id) {
        return new WrappedDataWatcherObject(id, I_CHAT_BASE_COMPONENT_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectOptionalIChatBaseComponent(final int id) {
        if (OPTIONAL_I_CHAT_BASE_COMPONENT_SERIALIZER == null) throw new UnsupportedOperationException(
                "Serialization of `Optional<IChatBaseComponent>` is unavailable"
        );

        return new WrappedDataWatcherObject(id, OPTIONAL_I_CHAT_BASE_COMPONENT_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectItemStack(final int id) {
        return new WrappedDataWatcherObject(id, ITEM_STACK_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectBoolean(final int id) {
        return new WrappedDataWatcherObject(id, BOOLEAN_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectVector3f(final int id) {
        return new WrappedDataWatcherObject(id, VECTOR_3F_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectBlockPosition(final int id) {
        return new WrappedDataWatcherObject(id, BLOCK_POSITION_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectOptionalBlockPosition(final int id) {
        return new WrappedDataWatcherObject(id, OPTIONAL_BLOCK_POSITION_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectEnumDirection(final int id) {
        return new WrappedDataWatcherObject(id, ENUM_DIRECTION_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectOptionalUUID(final int id) {
        return new WrappedDataWatcherObject(id, OPTIONAL_UUID_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectOptionalIBlockData(final int id) {
        return new WrappedDataWatcherObject(id, OPTIONAL_I_BLOCK_DATA_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectNBTTagCompound(final int id) {
        return new WrappedDataWatcherObject(id, NBT_TAG_COMPOUND_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectParticle(final int id) {
        if (PARTICLE_SERIALIZER == null) throw new UnsupportedOperationException(
                "Serialization of `Particle` is unavailable"
        );

        return new WrappedDataWatcherObject(id, PARTICLE_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectVillagerData(final int id) {
        if (VILLAGER_DATA_SERIALIZER == null) throw new UnsupportedOperationException(
                "Serialization of `VillagerData` is unavailable"
        );

        return new WrappedDataWatcherObject(id, VILLAGER_DATA_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectOptionalInt(final int id) {
        if (OPTIONAL_INT_SERIALIZER == null) throw new UnsupportedOperationException(
                "Serialization of `Optional<Integer>` is unavailable"
        );

        return new WrappedDataWatcherObject(id, OPTIONAL_INT_SERIALIZER);
    }

    private @NotNull WrappedDataWatcherObject watcherObjectEntityPose(final int id) {
        if (ENTITY_POSE_SERIALIZER == null) throw new UnsupportedOperationException(
                "Serialization of `EntityPose` is unavailable"
        );

        return new WrappedDataWatcherObject(id, ENTITY_POSE_SERIALIZER);
    }

    // Unsupported types

    private @NotNull WrappedDataWatcherObject watcherObjectShort(final int id) {
        if (SHORT_SERIALIZER == null) throw new UnsupportedOperationException(
                "Serialization of `Short` is unavailable"
        );

        return new WrappedDataWatcherObject(id, SHORT_SERIALIZER);
    }

    ///////////////////////////////////////////////////////////////////////////
    // #createWatchableObject(id, value)
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final byte value) {
        return new WrappedWatchableObject(watcherObjectByte(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final int value) {
        return new WrappedWatchableObject(watcherObjectInt(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final float value) {
        return new WrappedWatchableObject(watcherObjectFloat(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final String value) {
        return new WrappedWatchableObject(watcherObjectString(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableIChatBaseComponent(final int id,
                                                                             final @NonNull Object value) {
        return new WrappedWatchableObject(watcherObjectIChatBaseComponent(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptionalIChatBaseComponent(final int id,
                                                                                     final @Nullable Object value) {
        return new WrappedWatchableObject(watcherObjectOptionalIChatBaseComponent(id), Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableItemStack(final int id, final Object value) {
        return new WrappedWatchableObject(watcherObjectItemStack(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final boolean value) {
        return new WrappedWatchableObject(watcherObjectBoolean(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableVector3f(final int id, final @NotNull Object value) {
        return new WrappedWatchableObject(watcherObjectVector3f(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableBlockPosition(final int id, final @NotNull Object value) {
        return new WrappedWatchableObject(watcherObjectBlockPosition(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptionalBlockPosition(final int id,
                                                                                final @Nullable Object value) {
        return new WrappedWatchableObject(watcherObjectOptionalBlockPosition(id), Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableEnumDirection(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(watcherObjectEnumDirection(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptional(final int id, final @Nullable UUID value) {
        return new WrappedWatchableObject(watcherObjectOptionalUUID(id), Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptionalIBlockData(final int id,
                                                                             final @Nullable Object value) {
        return new WrappedWatchableObject(watcherObjectOptionalIBlockData(id), Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableNBTTagCompound(final int id, final @NotNull Object value) {
        return new WrappedWatchableObject(watcherObjectNBTTagCompound(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableParticle(final int id, final @NotNull Object value) {
        return new WrappedWatchableObject(watcherObjectParticle(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableVillagerData(final int id, final @NotNull Object value) {
        return new WrappedWatchableObject(watcherObjectVillagerData(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptional(final int id, final @Nullable Integer value) {
        return new WrappedWatchableObject(watcherObjectOptionalInt(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableEntityPose(final int id, final @NotNull Object value) {
        return new WrappedWatchableObject(watcherObjectEntityPose(id), value);
    }

    // Unsupported types

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final short value) {
        return new WrappedWatchableObject(watcherObjectShort(id), value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableObject(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class StandardDataWatcherModifier implements DataWatcherModifier {

        private final @NotNull WrappedDataWatcher dataWatcher;

        @Override
        public @NotNull WrappedDataWatcher dataWatcher() {
            return dataWatcher;
        }

        @Override
        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public @NotNull DataWatcherModifier clone() {
            return new StandardDataWatcherModifier(dataWatcher.deepClone());
        }

        // Actual types

        @Override
        public @NotNull DataWatcherModifier set(final int id, final byte value) {
            dataWatcher.setObject(watcherObjectByte(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final int value) {
            dataWatcher.setObject(watcherObjectInt(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final float value) {
            dataWatcher.setObject(watcherObjectFloat(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final @NonNull String value) {
            dataWatcher.setObject(watcherObjectString(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setIChatBaseComponent(final int id, final @NonNull Object value) {
            dataWatcher.setObject(watcherObjectIChatBaseComponent(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptionalIChatBaseComponent(final int id, final @Nullable Object value) {
            dataWatcher.setObject(watcherObjectOptionalIChatBaseComponent(id), Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setItemStack(int id, @NotNull Object value) {
            dataWatcher.setObject(watcherObjectItemStack(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final boolean value) {
            dataWatcher.setObject(watcherObjectBoolean(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setVector3f(final int id, final @NonNull Object value) {
            dataWatcher.setObject(watcherObjectVector3f(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setBlockPosition(final int id, final @NotNull Object value) {
            dataWatcher.setObject(watcherObjectBlockPosition(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptionalBlockPosition(final int id, final @Nullable Object value) {
            dataWatcher.setObject(watcherObjectOptionalBlockPosition(id), Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setEnumDirection(final int id, final @NonNull Object value) {
            dataWatcher.setObject(watcherObjectEnumDirection(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptional(final int id, final @Nullable UUID value) {
            dataWatcher.setObject(watcherObjectOptionalUUID(id), Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptionalIBlockData(final int id, final @Nullable Object value) {
            dataWatcher.setObject(watcherObjectOptionalIBlockData(id), Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setNBTTagCompound(final int id, final @NotNull Object value) {
            dataWatcher.setObject(watcherObjectNBTTagCompound(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setParticle(final int id, final @NotNull Object value) {
            dataWatcher.setObject(watcherObjectParticle(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setVillagerData(final int id, final @NotNull Object value) {
            dataWatcher.setObject(watcherObjectVillagerData(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptional(final int id, final @Nullable Integer value) {
            dataWatcher.setObject(watcherObjectOptionalInt(id), Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setEntityPose(final int id, final @NotNull Object value) {
            dataWatcher.setObject(watcherObjectEntityPose(id), value);

            return this;
        }
        // Unsupported types

        @Override
        public @NotNull DataWatcherModifier set(final int id, final short value) {
            dataWatcher.setObject(watcherObjectShort(id), value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setObject(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }
    }
}
