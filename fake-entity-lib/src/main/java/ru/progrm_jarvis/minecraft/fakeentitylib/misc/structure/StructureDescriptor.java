package ru.progrm_jarvis.minecraft.fakeentitylib.misc.structure;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Descriptor of {@link Structure} containing all its display information.
 * All its components are immutable unlike the very structure components
 * because descriptor is meant to be a simple data model of any structure rather than the structure itself.
 */
@Value
@Builder
public class StructureDescriptor {

    @Singular ImmutableList<Element> elements;

    @Builder.Default int frames = 0;

    /**
     * Keyframes of the structure.
     */
    Int2ObjectMap<FrameUpdater> keyframes;

    private static final Gson gson = new Gson();

    public static StructureDescriptor from(final @NonNull JsonRepresentation jsonRepresentation) {
        val elementNames = new ArrayList<String>();
        val elementList = new ArrayList<Element>();
        jsonRepresentation.getElements().forEach((name, element) -> {
            elementNames.add(name);
            elementList.add(element);
        });

        val descriptor = builder().elements(elementList); // add elements to descriptor
        val keyframesList = jsonRepresentation.getOrderedKeyframes();
        if (!keyframesList.isEmpty()) {
            val keyframes = new Int2ObjectOpenHashMap<FrameUpdater>();

            for (val keyframe : keyframesList) keyframes
                    .put(keyframe.tick, keyframe.toFrameUpdaterByElementNames(elementNames));

            descriptor.keyframes(keyframes);
        }

        return descriptor.build();
    }

    public static StructureDescriptor fromJson(final @NonNull String json) {
        return from(gson.fromJson(json, JsonRepresentation.class));
    }

    public static StructureDescriptor fromJson(final @NonNull Reader jsonReader) {
        return from(gson.fromJson(jsonReader, JsonRepresentation.class));
    }

    public static StructureDescriptor fromJson(final @NonNull JsonReader jsonReader) {
        return from(gson.fromJson(jsonReader, JsonRepresentation.class));
    }

    public static StructureDescriptor fromJson(final @NonNull JsonElement jsonElement) {
        return from(gson.fromJson(jsonElement, JsonRepresentation.class));
    }

    @SneakyThrows
    public static StructureDescriptor fromJson(final @NonNull File jsonFile) {
        try (val reader = new BufferedReader(new FileReader(jsonFile))) {
            return fromJson(reader);
        }
    }

    @FunctionalInterface
    public interface FrameUpdater {

        FrameUpdater EMPTY = structure -> {};

        void update(Structure structure);
    }

    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FastFrameUpdater implements FrameUpdater {

        int size;
        int[] ids;
        Structure.Element.Updater[] updaters;

        public static FrameUpdater from(final @NonNull int[] ids, final @NonNull Structure.Element.Updater[] updaters) {
            val idsLength = ids.length;
            Preconditions.checkArgument(idsLength == updaters.length, "ids length should be equal to updaters length");

            if (idsLength == 0) return EMPTY;
            return new FastFrameUpdater(idsLength, ids, updaters);
        }

        @Override
        public void update(final Structure structure) {
            for (int i = 0; i < ids.length; i++) structure.updateElement(ids[i], updaters[i]);
        }
    }

    @Value
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class Element {

        ItemStack item;
        Structure.Element.Size size;
        boolean visible;
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class JsonRepresentation {

        ElementData[] objects;
        Keyframe[] keyframes;

        public BiMap<String, Element> getElements() {
            return Arrays.stream(objects)
                    .map(JsonRepresentation.ElementData::getUserData)
                    .collect(Collectors.toMap(
                            JsonRepresentation.ElementData.Element::getCustomName,
                            JsonRepresentation.ElementData.Element::toElement, (e1, e2) -> {
                                throw new IllegalStateException("Duplicate element " + e1);
                            }, () -> HashBiMap.create(objects.length))
                    );
        }

        public List<Keyframe> getOrderedKeyframes() {
            return ImmutableList.copyOf(Arrays.stream(keyframes)
                    .sorted(Comparator.comparing(keyframe -> keyframe.tick))
                    .toArray(Keyframe[]::new)
            );
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static final class ElementData {

            Element userData;

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static final class Element {

                /**
                 * Name by which this element is specified in structure among others
                 */
                @SerializedName("customname") String customName;

                /**
                 * Name of the block id in Minecraft
                 */
                @SerializedName("rawid") String rawId;

                /**
                 * Additional NBT data of the item (if possible).
                 */
                @SerializedName("customnbt") String customNbt;

                String name; // internal
                boolean selected, // internal
                        isItem,
                /**
                 * Whether this element is rotated normally
                 * (in case its actual point does not match the one of its backend entity)
                 */
                        enableRotCenter,
                        isDynamic,
                /**
                 * Whether this element is visible or not.
                 */
                        visible,
                        justAdded, // internal
                        highlight; // internal

                /**
                 * Size of block (<code>small</code>, <code>medium</code>, <code>solid</code> or <code>large</code>)
                 */
                String size;

                @SerializedName("blockid") String blockId; // internal (seems to be `id:${rawid}`)

                Translation translation;

                public static Structure.Element.Size sizeFromName(final @NonNull String sizeName) {
                    switch (sizeName) {
                        case "small": return Structure.Element.Size.SMALL;
                        case "medium": return Structure.Element.Size.MEDIUM;
                        case "solid": return Structure.Element.Size.SOLID;
                        case "large": return Structure.Element.Size.LARGE;
                        default: throw new IllegalArgumentException("Unknown size name: " + sizeName);
                    }
                }

                public StructureDescriptor.Element toElement() {
                    val item = new ItemStack(Material.matchMaterial(rawId));
                    // TODO add NBT support

                    return StructureDescriptor.Element.builder()
                            .item(item)
                            .visible(visible)
                            .size(sizeFromName(size))
                            .build();
                }

                @Data
                @FieldDefaults(level = AccessLevel.PRIVATE)
                private static final class Translation {
                    double x, y, z;
                }
            }
        }

        @Data
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static final class Keyframe {

            @SerializedName("ontick") int tick;
            Element[] objects;

            public FrameUpdater toFrameUpdaterByElements(final @NonNull List<Element> elements) {
                final Map<Integer, Element> elementMap = Arrays.stream(objects)
                        .collect(Collectors.toMap(element -> {
                            val index = elements.indexOf(element);

                            if (index == -1) throw new IllegalArgumentException(
                                    "No element " + element + " in elements list"
                            );

                            return index;
                        }, element -> element));

                val ids = Ints.toArray(elementMap.keySet());
                return FastFrameUpdater.from(ids, Arrays.stream(ids)
                        .mapToObj(elementMap::get)
                        .map(Element::toElementUpdater)
                        .toArray(Structure.Element.Updater[]::new));
            }

            public FrameUpdater toFrameUpdaterByElementNames(final @NonNull List<String> elementNames) {
                final Map<Integer, Element> elementMap = Arrays.stream(objects)
                        .collect(Collectors.toMap(element -> {
                            val index = elementNames.indexOf(element.customName);

                            if (index == -1) throw new IllegalArgumentException(
                                    "No element name " + element.customName + " in elements' names list"
                            );

                            return index;
                        }, element -> element));

                val ids = Ints.toArray(elementMap.keySet());
                return FastFrameUpdater.from(ids, Arrays.stream(ids)
                        .mapToObj(elementMap::get)
                        .map(Element::toElementUpdater)
                        .toArray(Structure.Element.Updater[]::new));
            }

            @Data
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static final class Element {

                UUID uuid;
                double size;
                @SerializedName("customname") String customName;
                Vector position;
                Vector rotation;
                boolean visible;

                public Structure.Element.Updater toElementUpdater() {
                    // lambda parameters are stored locally not to store reference to parent Element
                    final double posX = position.getX(), posY = position.getY(), posZ = position.getZ(),
                            rotX = rotation.getX(), rotY = rotation.getY(), rotZ = rotation.getZ();
                    final boolean visible = this.visible;

                    return (element) -> {
                        element.setRotation(posX, posY, posZ);
                        element.setRotation(rotX, rotY, rotZ);
                        element.setVisible(visible);
                    };
                }
            }
        }
    }
}
