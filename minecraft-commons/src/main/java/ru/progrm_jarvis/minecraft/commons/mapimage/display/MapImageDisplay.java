package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import lombok.NonNull;
import org.bukkit.entity.Player;
import ru.progrm_jarvis.minecraft.commons.mapimage.MapImage;
import ru.progrm_jarvis.minecraft.commons.player.collection.PlayerContainer;

import java.util.Optional;

/**
 * Display of {@link MapImage}.
 * It is responsible for displaying actual images to the players and resolving map-ID conflicts.
 */
public interface MapImageDisplay extends PlayerContainer {

    /**
     * Gets the image displayed.
     *
     * @return image displayed
     */
    MapImage image();

    /**
     * Gets the map ID used by this map image display for the player specified.
     *
     * @param player player whose map ID for this image display to get
     * @return optional containing map ID used by this map image display for the player
     * or empty optional if none (this display is not used for the player)
     */
    @NonNull Optional<Number> getMapId(@NonNull Player player);
}
