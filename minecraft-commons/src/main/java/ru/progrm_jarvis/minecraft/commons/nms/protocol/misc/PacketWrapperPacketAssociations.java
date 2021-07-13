package ru.progrm_jarvis.minecraft.commons.nms.protocol.misc;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import ru.progrm_jarvis.javacommons.collection.MapFiller;
import ru.progrm_jarvis.javacommons.object.Pair;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;

import java.lang.invoke.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.invoke.LambdaMetafactory.metafactory;
import static java.lang.invoke.MethodType.methodType;

/**
 * Utility for linking ProtocolLib's packer-related objects.
 */
@UtilityClass
public class PacketWrapperPacketAssociations {

    private final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final MethodType FUNCTION__METHOD_TYPE = methodType(Function.class);
    private final MethodType VOID_PACKET_CONTAINER__METHOD_TYPE = methodType(void.class, PacketContainer.class);

    private final String FUNCTION__APPLY__METHOD_NAME = "apply";

    /**
     * Immutable bi-directional map of packet types and their IDs
     */
    @NonNull public final BiMap<PacketType, PacketTypeId> PACKET_TYPES = ImmutableBiMap.copyOf(
            MapFiller.from(new HashMap<PacketType, PacketTypeId>())
                    .fill(fieldPacketTypes(PacketType.Handshake.Client.class, "Handshake", PacketDirection.CLIENT))
                    .fill(fieldPacketTypes(PacketType.Handshake.Server.class, "Handshake", PacketDirection.SERVER))
                    .fill(fieldPacketTypes(PacketType.Login.Client.class, "Login", PacketDirection.CLIENT))
                    .fill(fieldPacketTypes(PacketType.Login.Server.class, "Login", PacketDirection.SERVER))
                    .fill(fieldPacketTypes(PacketType.Play.Client.class, "Play", PacketDirection.CLIENT))
                    .fill(fieldPacketTypes(PacketType.Play.Server.class, "Play", PacketDirection.SERVER))
                    .fill(fieldPacketTypes(PacketType.Status.Client.class, "Status", PacketDirection.CLIENT))
                    .fill(fieldPacketTypes(PacketType.Status.Server.class, "Status", PacketDirection.SERVER))
                    .map()
    );

    private final Map<PacketType, Function<PacketContainer, AbstractPacket>> PACKET_CREATORS
            = new ConcurrentHashMap<>();

    private Stream<Pair<PacketType, PacketTypeId>> fieldPacketTypes(final @NonNull Class<?> packetType,
                                                                    final @NonNull String group,
                                                                    final @NonNull PacketDirection direction) {
        return Arrays.stream(packetType.getDeclaredFields())
                .filter(field -> PacketType.class.isAssignableFrom(field.getType()))
                //.filter(field -> !field.isAnnotationPresent(Deprecated.class))
                .map(field -> {
                    final PacketType fieldValue;
                    try {
                        fieldValue = (PacketType) field.get(null);
                    } catch (final IllegalAccessException e) {
                        throw new IllegalStateException("Could not read value of field " + field);
                    }
                    return Pair.of(
                            fieldValue,
                            PacketTypeId.of(group, direction, upperCaseNameToUpperCamelCase(field.getName()))
                    );
                });
    }

    private String upperCaseNameToUpperCamelCase(final @NonNull String name) {
        val split = StringUtils.split(name, '_');

        val camelCase = new StringBuilder();
        for (val word : split)
            if (word.length() != 0) camelCase
                    .append(StringUtils.capitalize(StringUtils.lowerCase(word)));
            else camelCase.append("_");

        return camelCase.toString();
    }

    /**
     * Creates new packet wrapper of a valid type from the specified packet container object.
     *
     * @param packet packet to wrap using packet wrapper
     * @return created packet wrapper object for the packet
     */
    public AbstractPacket createPacketWrapper(final @NonNull PacketContainer packet) {
        return PACKET_CREATORS
                .computeIfAbsent(packet.getType(), packetType -> {
                    final Class<?> packetWrapperClass;
                    {
                        val className = PACKET_TYPES.get(packetType).toPacketWrapperClassName();
                        try {
                            packetWrapperClass = Class.forName(className);
                        } catch (final ClassNotFoundException e) {
                            throw new IllegalStateException("Could not find class by name \"" + className + '"');
                        }
                    }


                    MethodHandle constructorMethodHandle;
                    try {
                        constructorMethodHandle = LOOKUP.findConstructor(
                                packetWrapperClass, VOID_PACKET_CONTAINER__METHOD_TYPE
                        );
                    } catch (final NoSuchMethodException | IllegalAccessException e) {
                        throw new IllegalStateException(
                                "Cannot create method handle for constructor "
                                        + packetWrapperClass + "(PacketContainer)", e
                        );
                    }

                    val type = constructorMethodHandle.type();

                    final CallSite callSite;
                    try {
                        callSite = metafactory(
                                LOOKUP, FUNCTION__APPLY__METHOD_NAME, FUNCTION__METHOD_TYPE,
                                VOID_PACKET_CONTAINER__METHOD_TYPE, constructorMethodHandle, type
                        );
                    } catch (LambdaConversionException e) {
                        throw new IllegalStateException(
                                "Cannot invoke metafactory for constructor method-handle " + constructorMethodHandle, e
                        );
                    }

                    constructorMethodHandle = callSite.getTarget();

                    try {
                        //noinspection unchecked
                        return (Function<PacketContainer, AbstractPacket>) constructorMethodHandle.invokeExact();
                    } catch (final Throwable x) {
                        throw new IllegalStateException(
                                "Cannot invoke metafactory-provided method-handle " + constructorMethodHandle, x
                        );
                    }
                })
                .apply(packet);
    }

    /**
     * Direction of the packet.
     */
    @RequiredArgsConstructor
    private enum PacketDirection {

        /**
         * Packet going to client
         */
        CLIENT("Client"),

        /**
         * Packet going to the server
         */
        SERVER("Server");

        private final String name;
    }

    @Value(staticConstructor = "of")
    @FieldDefaults(level = AccessLevel.PROTECTED)
    private static class PacketTypeId {

        private static final String PACKET_WRAPPER_PACKAGE = SystemPropertyUtil.getSystemProperty(
                PacketTypeId.class.getCanonicalName() + "-packet-wrapper-package",
                Function.identity(), "com.comphenix.packetwrapper"
        );

        /**
         * Group of packets to which the one belongs
         */
        @NonNull String group;

        /**
         * Direction of the packet
         */
        @NonNull PacketDirection direction;

        /**
         * Name of the packet in the system
         */
        @NonNull String name;

        @NonNull
        private String toPacketWrapperClassName() {
            return PACKET_WRAPPER_PACKAGE + ".Wrapper" + group + direction.name() + name;
        }
    }
}
