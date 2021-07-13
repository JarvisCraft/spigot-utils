package ru.progrm_jarvis.minecraft.commons.nms;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.MaterialData;
import ru.progrm_jarvis.javacommons.collection.MapFiller;

import java.util.HashMap;
import java.util.Map;

/**
 * Various utilities used for supporting older versions of Minecraft-related environment,
 * such as changed enum names and updated method signatures.
 */
@UtilityClass
@SuppressWarnings("deprecation")
public class LegacySupport {

    private static final int NMS_VERSION_GENERATION = NmsUtil.getVersion().getGeneration();

    private static final boolean LEGACY_MATERIALS = NMS_VERSION_GENERATION < 13;
    private static final boolean LEGACY_BLOCK_FALLING = NMS_VERSION_GENERATION < 13;

    private static final Map<String, LegacyItem> legacyItems = MapFiller.from(new HashMap<String, LegacyItem>())
            // TODO or find better solution .put("CAVE_AIR", new LegacyItem("CAVE_AIR", "AIR"))
            // .put("VOID_AIR", new LegacyItem("VOID_AIR", "AIR"))
            .map();

    /**
     * Spawns a falling block at specified location.
     *
     * @param location location at which to spawn the block
     * @param block block from which to create the falling one
     * @return spawned falling block
     */
    public FallingBlock spawnFallingBlock(final @NonNull Location location, final @NonNull Block block) {
        if (LEGACY_BLOCK_FALLING) return location.getWorld()
                .spawnFallingBlock(location, block.getType(), block.getData());
        return location.getWorld().spawnFallingBlock(location, block.getBlockData());
    }

    /**
     * Spawns a falling block at specified location.
     *
     * @param location location at which to spawn the block
     * @param material material of the block
     * @param materialData legacy material data
     * @return spawned falling block
     */
    public FallingBlock spawnFallingBlock(final @NonNull Location location,
                                          final @NonNull Material material, final byte materialData) {
        if (LEGACY_BLOCK_FALLING) return location.getWorld()
                .spawnFallingBlock(location, material, materialData);
        return location.getWorld().spawnFallingBlock(location, new MaterialData(material, materialData));
    }

    private static LegacyItem legacyItem(final @NonNull Material material, final int legacyData) {
        return new LegacyItem(material, (byte) legacyData);
    }

    private static LegacyItem legacyItem(final @NonNull Material material) {
        return legacyItem(material, 0);
    }

    private static LegacyItem legacyItem(final @NonNull String materialName,
                                         final @NonNull String legacyMaterialName,
                                         final int legacyData) {
        return new LegacyItem(Material.valueOf(LEGACY_MATERIALS ? legacyMaterialName : materialName), legacyData);
    }

    private static LegacyItem legacyItem(final @NonNull String materialName,
                                         final @NonNull String legacyMaterialName) {
        return legacyItem(materialName, legacyMaterialName, 0);
    }

    @Value
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class LegacyItem {
        @NonNull Material material;
        final byte legacyData;

        private LegacyItem(final @NonNull Material material, final int legacyData) {
            this(material, (byte) legacyData);
        }

        private LegacyItem(final @NonNull Material material) {
            this(material, 0);
        }

        private LegacyItem(final @NonNull String materialName, final @NonNull String legacyMaterialName,
                           final int legacyData) {
            this(Material.valueOf(LEGACY_MATERIALS ? legacyMaterialName : materialName), (byte) legacyData);
        }

        private LegacyItem(final @NonNull String materialName, final @NonNull String legacyMaterialName) {
            this(materialName, legacyMaterialName, 0);
        }
    }
}
