package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * DataWatcher factory for pre 1.9 versions.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LegacyDataWatcherFactory implements DataWatcherFactory {

    public static @NotNull DataWatcherFactory create() {
        return new LegacyDataWatcherFactory();
    }

    @Override
    public @NotNull DataWatcherModifier modifier(final @NonNull WrappedDataWatcher watcher) {
        return new LegacyDataWatcherModifier(watcher);
    }

    @Override
    public @NotNull DataWatcherModifier modifier() {
        return new LegacyDataWatcherModifier(new WrappedDataWatcher());
    }

    // Actual types

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final byte value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final int value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final float value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final String value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableIChatBaseComponent(final int id,
                                                                             final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptionalIChatBaseComponent(final int id,
                                                                                     final @Nullable Object value) {
        return new WrappedWatchableObject(id, Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableItemStack(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final boolean value) {
        return createWatchable(id, value ? (byte) 0x1 : (byte) 0x0);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableVector3f(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableBlockPosition(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptionalBlockPosition(final int id,
                                                                                final @Nullable Object value) {
        return new WrappedWatchableObject(id, Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableEnumDirection(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptional(final int id, final @Nullable UUID value) {
        return new WrappedWatchableObject(id, Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptionalIBlockData(final int id,
                                                                             final @Nullable Object value) {
        return new WrappedWatchableObject(id, Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableNBTTagCompound(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableParticle(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableVillagerData(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableOptional(final int id, final @Nullable Integer value) {
        return new WrappedWatchableObject(id, Optional.ofNullable(value));
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableEntityPose(final int id, final @NonNull Object value) {
        return new WrappedWatchableObject(id, value);
    }

    // Unsupported types

    @Override
    public @NotNull WrappedWatchableObject createWatchable(final int id, final short value) {
        return new WrappedWatchableObject(id, value);
    }

    @Override
    public @NotNull WrappedWatchableObject createWatchableObject(final int id, final Object value) {
        return new WrappedWatchableObject(id, value);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class LegacyDataWatcherModifier implements DataWatcherFactory.DataWatcherModifier {

        @NotNull WrappedDataWatcher dataWatcher;

        @Override
        public @NotNull WrappedDataWatcher dataWatcher() {
            return dataWatcher;
        }

        @Override
        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public @NotNull DataWatcherFactory.DataWatcherModifier clone() {
            return new LegacyDataWatcherModifier(dataWatcher.deepClone());
        }

        // Actual types

        @Override
        public @NotNull DataWatcherModifier set(final int id, final byte value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final int value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final float value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final @NonNull String value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setIChatBaseComponent(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptionalIChatBaseComponent(final int id, final @Nullable Object value) {
            dataWatcher.setObject(id, Optional.ofNullable(value));

            return this;
        }


        @Override
        public @NotNull DataWatcherModifier setItemStack(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier set(final int id, final boolean value) {
            dataWatcher.setObject(id, value ? (byte) 0x1 : (byte) 0x0);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setVector3f(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setBlockPosition(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptionalBlockPosition(final int id, final @Nullable Object value) {
            dataWatcher.setObject(id, Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setEnumDirection(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptional(final int id, final @Nullable UUID value) {
            dataWatcher.setObject(id, Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptionalIBlockData(final int id, final @Nullable Object value) {
            dataWatcher.setObject(id, Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setNBTTagCompound(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setParticle(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setVillagerData(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setOptional(final int id, final @Nullable Integer value) {
            dataWatcher.setObject(id, Optional.ofNullable(value));

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setEntityPose(final int id, final @NonNull Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        // Unsupported types

        @Override
        public @NotNull DataWatcherModifier set(final int id, final short value) {
            dataWatcher.setObject(id, value);

            return this;
        }

        @Override
        public @NotNull DataWatcherModifier setObject(final int id, final Object value) {
            dataWatcher.setObject(id, value);

            return this;
        }
    }
}
