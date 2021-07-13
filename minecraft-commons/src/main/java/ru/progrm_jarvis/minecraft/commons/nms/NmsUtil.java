package ru.progrm_jarvis.minecraft.commons.nms;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.javacommons.invoke.InvokeUtil;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.DataWatcherFactory;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.LegacyDataWatcherFactory;
import ru.progrm_jarvis.minecraft.commons.nms.metadata.StandardDataWatcherFactory;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility for NMS-related features
 */
@UtilityClass
public class NmsUtil {

    private final NmsVersion NMS_VERSION = NmsVersion.computeCurrent();
    /**
     * Base package of NMS (<i>net.minecraft.server.{version}</i>)
     */
    private final String NMS_PACKAGE = "net.minecraft.server." + NMS_VERSION.getName(),
    /**
     * Base package of CraftBukkit (<i>org.bukkit.craftbukkit.{version}</i>)
     */
    CRAFT_BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION.getName();

    /**
     * Field {@code {nms}.Entity.entityCount} responsible for entity ID generation.
     *
     * @apiNote this field should never be used directly, it is only intended for initialization
     * of inner specific implementations of {@link EntityIdGenerator}.
     */
    private final @NotNull Field ENTITY_COUNT_FIELD;

    private final @NotNull EntityIdGenerator ENTITY_ID_GENERATOR;

    static {
        final Class<?> nmsEntityClass;
        {
            val nmsEntityClassName = getNmsPackage() + ".Entity";
            try {
                nmsEntityClass = Class.forName(nmsEntityClassName);
            } catch (final ClassNotFoundException e) {
                throw new InternalError("Cannot find entity class by name \"" + nmsEntityClassName + '"', e);
            }
        }
        try {
            ENTITY_COUNT_FIELD = nmsEntityClass.getDeclaredField("entityCount");
        } catch (NoSuchFieldException e) {
            throw new InternalError("Cannot find field " + nmsEntityClass.getCanonicalName() + "#entityCount", e);
        }

        final Class<?> entityCountFieldType;
        if (AtomicInteger.class.isAssignableFrom(entityCountFieldType
                = ENTITY_COUNT_FIELD.getType())) ENTITY_ID_GENERATOR = AtomicIntegerEntityIdGenerator.INSTANCE;
        else if (entityCountFieldType == int.class) ENTITY_ID_GENERATOR = IntEntityIdGenerator.INSTANCE;
        else throw new InternalError(
                    "Field " + ENTITY_COUNT_FIELD + " of class " + nmsEntityClass + " has an unknown type"
            );
    }

    /**
     * DataWatcher factory valid for current server version
     */
    private final DataWatcherFactory DATA_WATCHER_FACTORY = NMS_VERSION.getGeneration() < 9
            ? LegacyDataWatcherFactory.create() : StandardDataWatcherFactory.create();

    /**
     * Gets version of the current server.
     *
     * @return version of this server
     */
    public NmsVersion getVersion() {
        return NMS_VERSION;
    }

    /**
     * Gets base package of NMS (<i>net.minecraft.server.{version}</i>)
     *
     * @return base package of NMS
     */
    public String getNmsPackage() {
        return NMS_PACKAGE;
    }

    /**
     * Gets base package of CraftBukkit (<i>org.bukkit.craftbukkit.{version}</i>)
     *
     * @return base package of CraftBukkit
     */
    public String getCraftBukkitPackage() {
        return CRAFT_BUKKIT_PACKAGE;
    }

    /**
     * Gets DataWatcher factory valid for current server version.
     *
     * @return DataWatcher factory valid for current server version
     */
    public static DataWatcherFactory getDataWatcherFactory() {
        return DATA_WATCHER_FACTORY;
    }

    /**
     * Creates new DataWatcher modifier valid for current server version.
     *
     * @return DataWatcher modifier valid for current server version
     */
    public DataWatcherFactory.DataWatcherModifier dataWatcherModifier() {
        return DATA_WATCHER_FACTORY.modifier();
    }

    /**
     * Creates new DataWatcher modifier from DataWatcher specified which valid for current server version.
     *
     * @param dataWatcher DataWatcher from which to create DataWatcher modifier
     * @return DataWatcher modifier valid for current server version
     */
    public DataWatcherFactory.DataWatcherModifier dataWatcherModifier(final @NonNull WrappedDataWatcher dataWatcher) {
        return DATA_WATCHER_FACTORY.modifier(dataWatcher);
    }

    /**
     * Gets ID for entity preventing from conflicts with real entities.
     *
     * @return new ID for an entity
     */
    @SneakyThrows
    public int nextEntityId() {
        return ENTITY_ID_GENERATOR.nextId();
    }

    /**
     * Version of a server.
     */
    @Value
    @RequiredArgsConstructor
    public static class NmsVersion {

        /**
         * Name of the version
         */
        @NonNull String name;

        /**
         * Generation of a version (such as <b>13</b> for minecraft <i>1.<b>13</b>.2</i>)
         */
        short generation;

        /**
         * Constructs a new NMS version by name specified (such as <i>v1_12_R1</i>).
         *
         * @param name name of a version
         */
        private NmsVersion(final @NonNull String name) {
            this(name, Short.parseShort(name.substring(3, name.indexOf('_', 4))));
        }

        /**
         * Computes current version of NMS for this server
         * based on implementation of {@link org.bukkit.Server} accessible via {@link Bukkit#getServer()}.
         *
         * @return current NMS version
         */
        public static NmsVersion computeCurrent() {
            val craftServerPackage = Bukkit.getServer().getClass().getPackage().getName();

            return new NmsVersion(craftServerPackage.substring(craftServerPackage.lastIndexOf('.') + 1));
        }
    }

    @FunctionalInterface
    private interface EntityIdGenerator {
        int nextId();
    }

    /**
     * Implementation of {@link EntityIdGenerator} based on {@code int} {@link #ENTITY_COUNT_FIELD}.
     *
     * @implNote this implementation does allow ID generation
     * only from {@link Bukkit#isPrimaryThread() Bukkit's primary thread}.
     */
    private final class IntEntityIdGenerator implements EntityIdGenerator {

        private static final @NotNull EntityIdGenerator INSTANCE = new AtomicIntegerEntityIdGenerator();

        /**
         * Getter method-handle of {@link #ENTITY_COUNT_FIELD}.
         */
        private static final @NotNull MethodHandle ENTITY_COUNT_FIELD__GETTER;

        /**
         * Getter method-handle of {@link #ENTITY_COUNT_FIELD}.
         */
        private static final @NotNull MethodHandle ENTITY_COUNT_FIELD__SETTER;

        static {
            ENTITY_COUNT_FIELD__GETTER = InvokeUtil.toGetterMethodHandle(ENTITY_COUNT_FIELD);
            ENTITY_COUNT_FIELD__SETTER = InvokeUtil.toSetterMethodHandle(ENTITY_COUNT_FIELD);
        }

        @Override
        @SneakyThrows // MethodHandles invocation
        public int nextId() {
            if (!Bukkit.isPrimaryThread()) throw new IllegalStateException(
                    "Entity IDs should only be generated on main Bukkit thread"
            );

            val id = (int) ENTITY_COUNT_FIELD__GETTER.invokeExact();
            ENTITY_COUNT_FIELD__SETTER.invokeExact(id + 1);

            return id;
        }
    }

    /**
     * Implementation of {@link EntityIdGenerator} based on {@link AtomicInteger} {@link #ENTITY_COUNT_FIELD}.
     */
    private final class AtomicIntegerEntityIdGenerator implements EntityIdGenerator {

        private static final @NotNull EntityIdGenerator INSTANCE = new AtomicIntegerEntityIdGenerator();

        private static final @NotNull AtomicInteger VALUE;

        static {
            final MethodHandle getter = InvokeUtil.toGetterMethodHandle(ENTITY_COUNT_FIELD);
            try {
                VALUE = (AtomicInteger) getter.invokeExact();
            } catch (final Throwable x) {
                throw new InternalError("Could not get the value of field " + ENTITY_COUNT_FIELD);
            }
        }

        @Override
        public int nextId() {
            return VALUE.incrementAndGet();
        }
    }
}
