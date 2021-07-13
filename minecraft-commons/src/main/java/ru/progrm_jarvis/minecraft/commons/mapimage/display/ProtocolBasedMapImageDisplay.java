package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import com.comphenix.packetwrapper.WrapperPlayServerMap;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.javacommons.collection.MapUtil;
import ru.progrm_jarvis.minecraft.commons.mapimage.MapImage;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistries;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistry;
import ru.progrm_jarvis.minecraft.commons.player.registry.PlayerRegistryRegistration;

import java.util.*;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProtocolBasedMapImageDisplay implements MapImageDisplay {

    @NonNull MapImage image;
    @NonNull Map<Player, MapView> playerMaps;
    @NonNull Set<Player> playersView;
    @Getter boolean global;

    @PlayerRegistryRegistration(PlayerRegistryRegistration.Policy.AUTO)
    public ProtocolBasedMapImageDisplay(final @NonNull MapImage image, final @NonNull Map<Player, MapView> playerMaps,
                                        final @NonNull Plugin plugin, final boolean global,
                                        final @NonNull PlayerRegistry playerRegistry) {
        this.image = image;
        this.playerMaps = playerMaps;
        playersView = Collections.unmodifiableSet(playerMaps.keySet());
        this.global = global;

        playerRegistry.register(this);
        image.subscribeOnUpdates(this::sendDeltaToAllPlayers);
    }

    @PlayerRegistryRegistration(PlayerRegistryRegistration.Policy.AUTO)
    public ProtocolBasedMapImageDisplay(final @NonNull MapImage image, final @NonNull Map<Player, MapView> playerMaps,
                                        final @NonNull Plugin plugin, final boolean global) {
        this(image, playerMaps, plugin, global, PlayerRegistries.defaultRegistry(plugin));
    }

    @Override
    public MapImage image() {
        return image;
    }

    /**
     * Sends the whole image to the players.
     *
     * @param player player to whom the image should be sent
     */
    protected void sendFullImage(final @NonNull Player player) {
        new WrapperPlayServerMap() {{
            setItemDamage(PlayerMapManager.getMapId(playerMaps.get(player)));
            setScale(image.getDisplay());
            setColumns(image.getWidth());
            setRows(image.getHeight());
            setX(0);
            setZ(0);
            setData(image.getMapData());
        }}.sendPacket(player);
    }

    protected WrapperPlayServerMap newDeltaPacket(final @Nullable MapImage.Delta delta) {
        return new WrapperPlayServerMap() {{
            setScale(image.getDisplay());
            setColumns(delta.width());
            setRows(delta.height());
            setX(delta.leastX());
            setZ(delta.leastY());
            setData(image.getMapData(delta));
        }};
    }

    protected void sendDelta(final @NonNull Player player, final @NonNull MapImage.Delta delta) {
        if (delta.isEmpty()) return;

        val packet = newDeltaPacket(delta);
        packet.setItemDamage(PlayerMapManager.getMapId(playerMaps.get(player)));

        packet.sendPacket(player);
    }

    protected void sendDeltaToAllPlayers(final @NonNull MapImage.Delta delta) {
        if (delta.isEmpty()) return;

        val packet = newDeltaPacket(delta);

        for (val entry : playerMaps.entrySet()) {
            packet.setItemDamage(PlayerMapManager.getMapId(entry.getValue()));
            packet.sendPacket(entry.getKey());
        }
    }

    @Override
    public void addPlayer(final Player player) {
        // computeIfAbsent not to allocate the ID if the player is already contained (and so has ID allocated)
        playerMaps.computeIfAbsent(player, p -> {
            val map = PlayerMapManager.allocateMap(player);
            // although the rendering is contextual there is no need to use Bukkit's contextual renderer
            map.addRenderer(BlankMapRenderer.NON_CONTEXTUAL);

            return map;
        });
        sendFullImage(player);
    }

    @Override
    public void removePlayer(final Player player) {
        val map = playerMaps.remove(player);
        if (map != null) /* null-check just in case */ PlayerMapManager.freeMap(player, map);
    }

    @Override
    public boolean containsPlayer(final Player player) {
        return playerMaps.containsKey(player);
    }

    @Override
    public Collection<? extends Player> getPlayers() {
        return playersView;
    }

    @Override
    @NonNull
    public Optional<Number> getMapId(final @NonNull Player player) {
        return MapUtil.<Player, MapView, Optional<Number>>getOrDefault(
                playerMaps, player, map -> Optional.of(PlayerMapManager.getMapId(map)), Optional::empty
        );
    }
}
