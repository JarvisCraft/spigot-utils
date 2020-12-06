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
import ru.progrm_jarvis.minecraft.commons.util.function.UncheckedFunction;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.invoke.LambdaMetafactory.metafactory;

/**
 * Utility for linking ProtocolLib's packer-related objects.
 */
@UtilityClass
public class PacketWrapperPacketAssociations {

    private final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final MethodType FUNCTION__METHOD_TYPE = MethodType.methodType(Function.class);

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
                //.filter(field -> field.isAnnotationPresent(Deprecated.class))
                .map((UncheckedFunction<Field, Pair<PacketType, PacketTypeId>>) field -> Pair.of(
                        (PacketType) field.get(null),
                        PacketTypeId.of(group, direction, upperCaseNameToUpperCamelCase(field.getName()))
                ));
    }

    private String upperCaseNameToUpperCamelCase(final @NonNull String name) {
        val split = StringUtils.split(name, '_');

        val camelCase = new StringBuilder();
        for (val word : split) if (word.length() != 0) camelCase
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
                .computeIfAbsent(packet.getType(),
                        (UncheckedFunction<PacketType, Function<PacketContainer, AbstractPacket>>) packetType -> {
                            val packetWrapperClass = Class
                                    .forName(PACKET_TYPES.get(packetType).toPacketWrapperClassName());

                            val methodHandle = LOOKUP.unreflectConstructor(
                                    packetWrapperClass.getDeclaredConstructor(PacketContainer.class)
                            );

                            val type = methodHandle.type();

                            //noinspection unchecked
                            return (Function<PacketContainer, AbstractPacket>) metafactory(
                                    LOOKUP, FUNCTION__APPLY__METHOD_NAME, FUNCTION__METHOD_TYPE,
                                    type.generic(), methodHandle, type
                            ).getTarget().invokeExact();
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
        final @NonNull String group;

        /**
         * Direction of the packet
         */
        final @NonNull PacketDirection direction;

        /**
         * Name of the packet in the system
         */
        final @NonNull String name;

        @NonNull private String toPacketWrapperClassName() {
            return PACKET_WRAPPER_PACKAGE + ".Wrapper" + group + direction.name + name;
        }
    }
}
