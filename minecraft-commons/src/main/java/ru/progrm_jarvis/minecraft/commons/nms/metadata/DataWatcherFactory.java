package ru.progrm_jarvis.minecraft.commons.nms.metadata;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.nms.ProtocolLibConversions;

import java.util.UUID;

public interface DataWatcherFactory {

    // Modifier creation

    /**
     * Creates new modifier for {@link WrappedDataWatcher} specified.
     *
     * @param watcher which to use as modifier backend
     * @return created modifier
     */
    @NotNull DataWatcherModifier modifier(WrappedDataWatcher watcher);

    /**
     * Creates new modifier of {@link WrappedDataWatcher}.
     *
     * @return created modifier
     */
    @NotNull DataWatcherModifier modifier();

    // Actual types

    /**
     * Creates watchable object for {@code byte} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchable(int id, byte value);

    /**
     * Creates watchable object for {@code int} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchable(int id, int value);

    /**
     * Creates watchable object for {@code float} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchable(int id, float value);

    /**
     * Creates watchable object for {@link String} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchable(int id, String value);

    /**
     * Creates watchable object for {@code IChatBaseComponent} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableIChatBaseComponent(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link WrappedChatComponent} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull WrappedChatComponent value) {
        return createWatchableIChatBaseComponent(id, value.getHandle());
    }

    /**
     * Creates watchable object for optional {@code IChatBaseComponent} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableOptionalIChatBaseComponent(int id,
                                                                              @Nullable Object value);

    /**
     * Creates watchable object for optional {@link WrappedChatComponent} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchableOptional(final int id,
                                                                    final @Nullable WrappedChatComponent value) {
        return createWatchableOptionalIChatBaseComponent(id, value == null ? null : value.getHandle());
    }

    /**
     * Creates watchable object for <i>NMS</i> {@code ItemStack} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableItemStack(int id, Object value);

    /**
     * Creates watchable object for {@link ItemStack} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final ItemStack value) {
        return createWatchableItemStack(id, MinecraftReflection.getMinecraftItemStack(value));
    }

    /**
     * Creates watchable object for {@code boolean} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchable(int id, boolean value);

    /**
     * Creates watchable object for {@code Vector3f} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableVector3f(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link Vector3F} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull Vector3F value) {
        return createWatchableVector3f(id, ProtocolLibConversions.toNms(value));
    }

    /**
     * Creates watchable object for {@code BlockPosition} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableBlockPosition(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link BlockPosition} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull BlockPosition value) {
        return createWatchableBlockPosition(id, ProtocolLibConversions.toNms(value));
    }

    /**
     * Creates watchable object for optional {@code BlockPosition} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableOptionalBlockPosition(int id, @Nullable Object value);

    /**
     * Creates watchable object for optional {@link BlockPosition} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchableOptional(final int id, final @Nullable BlockPosition value) {
        return createWatchableOptionalBlockPosition(id, ProtocolLibConversions.toBlockPosition(value));
    }

    /**
     * Creates watchable object for {@code EnumDirection} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableEnumDirection(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link Direction} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull Direction value) {
        return createWatchableEnumDirection(id, ProtocolLibConversions.toNms(value));
    }

    /**
     * Creates watchable object for optional {@link UUID} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableOptional(int id, @Nullable UUID value);

    /**
     * Creates watchable object for optional {@code IBlockData} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableOptionalIBlockData(int id, @Nullable Object value);

    /**
     * Creates watchable object for optional {@link WrappedBlockData} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchableOptional(final int id,
                                                                    final @Nullable WrappedBlockData value) {
        return createWatchableOptionalIBlockData(id, value == null ? null : value.getHandle());
    }

    /**
     * Creates watchable object for {@code NBTTagCompound} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableNBTTagCompound(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@code NBTTagCompound} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull NbtCompound value) {
        return createWatchableNBTTagCompound(id, value.getHandle());
    }

    /**
     * Creates watchable object for {@code Particle} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableParticle(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link EnumWrappers.Particle} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull WrappedParticle<?> value) {
        return createWatchableParticle(id, value.getHandle());
    }

    /**
     * Creates watchable object for {@code VillagerData} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableVillagerData(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link EnumWrappers.Particle} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id, final @NonNull WrappedVillagerData value) {
        return createWatchableVillagerData(id, value.getHandle());
    }

    /**
     * Creates watchable object for optional {@code int} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableOptional(int id, @Nullable Integer value);

    /**
     * Creates watchable object for {@code EntityPose} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchableEntityPose(int id, @NonNull Object value);

    /**
     * Creates watchable object for {@link EnumWrappers.EntityPose} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    default @NotNull WrappedWatchableObject createWatchable(final int id,
                                                            final @NonNull EnumWrappers.EntityPose value) {
        return createWatchableEntityPose(id, value.toNms());
    }

    // Unsupported types

    /**
     * Creates watchable object for {@code short} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     */
    @NotNull WrappedWatchableObject createWatchable(int id, short value);

    /**
     * Creates watchable object for {@link Object} value at index specified.
     *
     * @param id id of a value
     * @param value value
     * @return created watchable object
     *
     * @apiNote this should only be used if there is no type-safe on NMS-specific analog
     */
    // should be used if and only if none of default #set[..](id, value) methods don't provide type given
    @NotNull WrappedWatchableObject createWatchableObject(int id, Object value);

    /**
     * Modifier of {@link WrappedDataWatcher} which applies all changes to DataWatcher.
     */
    interface DataWatcherModifier {

        /**
         * Returns DataWatcher modified.
         *
         * @return DataWatcher modified
         */
        @NotNull WrappedDataWatcher dataWatcher();

        /**
         * Returns deep clone of DataWatcher modified.
         *
         * @return clone of DataWatcher modified
         */
        default @NotNull WrappedDataWatcher dataWatcherClone() {
            return dataWatcher().deepClone();
        }

        /**
         * Deeply clones this modifier to a new one.
         *
         * @return this modifier's copy
         */
        @NotNull DataWatcherModifier clone();

        // Actual types

        /**
         * Sets DataWatcher's modifier to specified {@code byte} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier set(int id, byte value);

        /**
         * Sets DataWatcher's modifier to specified {@code int} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier set(int id, int value);

        /**
         * Sets DataWatcher's modifier to specified {@code float} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier set(int id, float value);

        /**
         * Sets DataWatcher's modifier to specified {@link String} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier set(int id, @NonNull String value);

        /**
         * Sets DataWatcher's modifier to specified {@code IChatBaseComponent} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setIChatBaseComponent(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link WrappedChatComponent} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull WrappedChatComponent value) {
            return setIChatBaseComponent(id, value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified optional {@code IChatBaseComponent} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setOptionalIChatBaseComponent(int id, @Nullable Object value);

        /**
         * Sets DataWatcher's modifier to specified optional {@link WrappedChatComponent} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier setOptional(final int id, final @Nullable WrappedChatComponent value) {
            return setOptionalIChatBaseComponent(id, value == null ? null : value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified <i>NMS</i> {@code ItemStack} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setItemStack(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link ItemStack} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull ItemStack value) {
            return setItemStack(id, MinecraftReflection.getMinecraftItemStack(value));
        }

        /**
         * Sets DataWatcher's modifier to specified {@code boolean} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier set(int id, boolean value);

        /**
         * Sets DataWatcher's modifier to specified {@code Vector3f} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setVector3f(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link Vector3F} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull Vector3F value) {
            return setVector3f(id, ProtocolLibConversions.toNms(value));
        }

        /**
         * Sets DataWatcher's modifier to specified {@code BlockPosition} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setBlockPosition(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link BlockPosition} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(int id, final @NonNull BlockPosition value) {
            return setBlockPosition(id, ProtocolLibConversions.toNms(value));
        }

        /**
         * Sets DataWatcher's modifier to specified optional {@code BlockPosition} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setOptionalBlockPosition(int id, @Nullable Object value);

        /**
         * Sets DataWatcher's modifier to specified optional {@link BlockPosition} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier setOptional(final int id, final @Nullable BlockPosition value) {
            return setOptionalBlockPosition(id, ProtocolLibConversions.toNms(value));
        }

        /**
         * Sets DataWatcher's modifier to specified {@code EnumDirection} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setEnumDirection(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link Direction} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull Direction value) {
            return setEnumDirection(id, ProtocolLibConversions.toNms(value));
        }

        /**
         * Sets DataWatcher's modifier to specified optional {@link UUID} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setOptional(int id, @Nullable UUID value);

        /**
         * Sets DataWatcher's modifier to specified optional {@code IBlockData} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setOptionalIBlockData(int id, @Nullable Object value);

        /**
         * Sets DataWatcher's modifier to specified optional {@link WrappedBlockData} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier setOptional(final int id, final @Nullable WrappedBlockData value) {
            return setOptionalIBlockData(id, value == null ? null : value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified {@code NBTTagCompound} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setNBTTagCompound(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link NbtCompound} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull NbtCompound value) {
            return setNBTTagCompound(id, value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified {@code Particle} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setParticle(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link NbtCompound} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull WrappedParticle<?> value) {
            return setParticle(id, value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified {@code VillagerData} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setVillagerData(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link WrappedVillagerData} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull WrappedVillagerData value) {
            return setVillagerData(id, value.getHandle());
        }

        /**
         * Sets DataWatcher's modifier to specified optional {@code int} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setOptional(int id, @Nullable Integer value);

        /**
         * Sets DataWatcher's modifier to specified {@code VillagerData} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier setEntityPose(int id, @NonNull Object value);

        /**
         * Sets DataWatcher's modifier to specified {@link EnumWrappers.EntityPose} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        default @NotNull DataWatcherModifier set(final int id, final @NonNull EnumWrappers.EntityPose value) {
            return setEntityPose(id, value.toNms());
        }

        // Unsupported types

        /**
         * Sets DataWatcher's modifier to specified {@code short} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         */
        @NotNull DataWatcherModifier set(int id, short value);

        /**
         * Sets DataWatcher's modifier to specified {@link Object} value at specified index.
         *
         * @param id id of a value
         * @param value value to set at id
         * @return this DataWatcher builder
         *
         * @apiNote this should only be used if there is no type-safe on NMS-specific analog
         */
        @NotNull DataWatcherModifier setObject(int id, Object value);
    }
}
