package ru.progrm_jarvis.minecraft.commons.mapimage.display;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

@ToString
@EqualsAndHashCode(callSuper = true)
public class BlankMapRenderer extends MapRenderer {

    public static final BlankMapRenderer
            CONTEXTUAL = new BlankMapRenderer(true),
            NON_CONTEXTUAL = new BlankMapRenderer(false);

    public BlankMapRenderer(final boolean contextual) {
        super(contextual);
    }

    @Override
    public void render(final MapView map, final MapCanvas canvas, final Player player) {}
}
