package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import ru.progrm_jarvis.javacommons.invoke.InvokeUtil;
import ru.progrm_jarvis.minecraft.commons.MinecraftCommons;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility responsible to allocate minimal amount of {@link MapView} for internal usage.
 * <p>
 * This allocates IDs once needed and reuses them as much as possible, so that:
 * <ul>
 *     <li>Two players can see different images by one same ID at the same time</li>
 *     <li>Any player can see different images by single ID at different time</li>
 *     <li>IDs are stored and reused at server restarts so that no useless IDs allocations occur</li>
 * </ul>
 * <p>
 * This requires the developer to free maps using {@link #freeMap(Player, MapView)}
 * (obtained from {@link #allocateMap(Player)}) once those are no longer required in order to use those optimally.
 */
@UtilityClass
public class PlayerMapManager {

    /**
     * Flag describing whether {@code int} or {@code short} map IDs are used by current server.
     */
    private final boolean USE_INT_IDS;

    /**
     * Method handle for {@link MapView#getId()} because it returns
     * {@code short} and {@code int} on different Bukkit API versions.
     */
    private final MethodHandle MAP_VIEW__GET_ID__METHOD,
    /**
     * Method handle for {@link Bukkit#getMap(int)} because it consumes
     * {@code short} and {@code int} on different Bukkit API versions.
     */
     BUKKIT__GET_MAP__METHOD;

    static {
        {
            final Method method;
            try {
                method = MapView.class.getDeclaredMethod("getId");
            } catch (final NoSuchMethodException e) {
                throw new IllegalStateException("Cannot find method " + MapView.class.getCanonicalName() + "#getId()");
            }
            val returnType = method.getReturnType();
            if (returnType == int.class) USE_INT_IDS = true;
            else if (returnType == short.class) USE_INT_IDS = false;
            else throw new IllegalStateException("Unknown return type of MapView#getId() method (" + returnType + ")");

            MAP_VIEW__GET_ID__METHOD = InvokeUtil.toMethodHandle(method);
        }
        try {
            BUKKIT__GET_MAP__METHOD = InvokeUtil.toMethodHandle(
                    Bukkit.class.getDeclaredMethod("getMap", USE_INT_IDS ? int.class : short.class)
            );
        } catch (final NoSuchMethodException e) {
            throw new IllegalStateException("Cannot find method " + Bukkit.class.getCanonicalName()
                    + "#getMap(" + (USE_INT_IDS ? "int" : "short") + ")");
        }
    }

    /**
     * Root directory of {@link PlayerMapManager} in which internal data is stored between sessions.
     */
    private final File ROOT_DIRECTORY = new File(MinecraftCommons.ROOT_DIRECTORY,
            SystemPropertyUtil.getSystemProperty(
                    PlayerMapManager.class.getCanonicalName() + ".internal-ids-list-file-name", Function.identity(),
                    "player_map_id_manager/"
            )
    );

    /**
     * File containing IDs of allocated maps.
     */
    private final File IDS_LIST_FILE = new File(ROOT_DIRECTORY, "map_ids.list");

    /**
     * Maps allocated in Bukkit for internal usage.
     */
    private final Set<MapView> ALLOCATED_MAPS;

    /**
     * Maps of {@link #ALLOCATED_MAPS} available for the player.
     * Similar maps may be related to different players as the image logic doesn't intersect.
     */
    private final SetMultimap<Player, MapView> PLAYER_MAPS;

    // static initialization of file-related stuff
    static {
        if (!ROOT_DIRECTORY.isFile()) try {
            Files.createDirectories(ROOT_DIRECTORY.toPath());
        } catch (final IOException e) {
            throw new RuntimeException("Couldn't create source directory of PlayerMapManager", e);
        }

        // Loads the maps stored in a file between sessions
        if (IDS_LIST_FILE.isFile()) {
            final List<String> fileLines;
            try {
                fileLines = Files.readAllLines(IDS_LIST_FILE.toPath());
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Couldn't read file " + IDS_LIST_FILE.getName() + " of PlayerMapManager ", e
                );
            }

            ALLOCATED_MAPS = fileLines.stream()
                    .filter(line -> !line.isEmpty() && line.indexOf('#') != 0)
                    .map(line -> getMap(Integer.parseInt(line)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()); // HashSet is used by default

            Bukkit.getLogger().info(
                    "Loaded " + ALLOCATED_MAPS.size() + " internally allocated world map IDs: " + ALLOCATED_MAPS
            );
        } else {
            try {
                Files.createFile(IDS_LIST_FILE.toPath());
            } catch (final IOException e) {
                throw new RuntimeException(
                        "Couldn't create file " + IDS_LIST_FILE.getName() + " of PlayerMapManager ", e
                );
            }
            ALLOCATED_MAPS = new HashSet<>();
        }

        // use optimal key size for allocatedMap
        PLAYER_MAPS = HashMultimap.create(
                Math.max(8, ALLOCATED_MAPS.size()), Math.max(16, Bukkit.getOnlinePlayers().size())
        );
    }

    /**
     * Gets {@link MapView}'s ID independently on version.
     *
     * @param mapView {@link MapView} whose {@link MapView#getId()} to invoke
     * @return map view's ID
     */
    @SneakyThrows
    public int getMapId(final @NonNull MapView mapView) {
        if (USE_INT_IDS) return (int) MAP_VIEW__GET_ID__METHOD.invokeExact(mapView);
        return (int) (short) MAP_VIEW__GET_ID__METHOD.invokeExact(mapView);
    }

    /**
     * Gets the {@link MapView} from the given ID independently on version.
     *
     * @param mapId id of the map to get
     * @return a map view if it exists, or null otherwise
     */
    @SneakyThrows
    public MapView getMap(final int mapId) {
        return USE_INT_IDS // note: same casts at both places to be able to use `invokeExact`
                ? (MapView) BUKKIT__GET_MAP__METHOD.invokeExact(mapId)
                : (MapView) BUKKIT__GET_MAP__METHOD.invokeExact((short) mapId);
    }

    /**
     * Allocates new map instance.
     *
     * @return newly allocated map with only blank renderer applied
     *
     * @apiNote should be called only in case of need
     */
    @Synchronized
    private MapView allocateNewMap() {
        final MapView map;
        {
            val worlds = Bukkit.getWorlds();
            if (worlds.isEmpty()) throw new IllegalStateException("There are no Bukkit worlds available");
            map = Bukkit.createMap(worlds.get(0));
        }

        try {
            Files.write(
                    IDS_LIST_FILE.toPath(),
                    (getMapId(map) + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND
            );
        } catch (final IOException e) {
            throw new RuntimeException(
                    "Couldn't write newly allocated ID to " + IDS_LIST_FILE.getName() + " of PlayerMapManager", e
            );
        }

        // clear renderers for map
        for (val renderer : map.getRenderers()) map.removeRenderer(renderer);
        ALLOCATED_MAPS.add(map);

        return map;
    }

    /**
     * Allocates new map view for player.
     * This returns a map view which is stored in this manager and is free for the player at the moment.
     * The map should be freed for the player once he can't see it / he leaves the server.
     *
     * @param player player for whom to allocate the map
     * @return map allocated for the player
     *
     * @see #freeMap(Player, MapView) should be called whenever the player stops seeing this map or leaves the server
     */
    @Synchronized
    public MapView allocateMap(final @NonNull Player player) {
        val mapsOfPlayer = PLAYER_MAPS.get(player);

        // if the player has all maps allocated of available than allocate another one (specially for him <3)
        // this is a fast equivalent of iterating through each allocated map
        // because only maps from allocatedMaps may be contained in mapsOfPlayers which is a Set
        if (mapsOfPlayer.size() == ALLOCATED_MAPS.size()) {
            val map = allocateNewMap();
            mapsOfPlayer.add(map);

            return map;
        }
        //otherwise take one of the available maps for him
        for (val map : ALLOCATED_MAPS) if (!mapsOfPlayer.contains(map)) {
            mapsOfPlayer.add(map);

            return map;
        }

        // this should never be reached
        throw new IllegalStateException(
                "Something went wrong, could not find any allocated map for the player although there should be one"
        );
    }

    /**
     * Frees the map for the player so that it can be reused.
     * This should be called whenever the player stops seeing this map or leaves the server.
     *
     * @param player player for whom to free the map
     * @param map map which should be freed
     * @apiNote this <b>must</b> be called for any map allocation once it can be free
     *
     * @see #allocateMap(Player) only obtained by calling this method should be freed
     */
    @Synchronized
    public void freeMap(final @NonNull Player player, final @NonNull MapView map) {
        PLAYER_MAPS.remove(player, map);
        for (val renderer : map.getRenderers()) map.removeRenderer(renderer);
    }
}
