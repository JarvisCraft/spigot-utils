package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Base for most common implementations of {@link ObservableFakeEntity}.
 */
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractObservableFakeEntity extends AbstractFakeEntity implements ObservableFakeEntity {

    @Getter boolean global;
    @Getter int viewDistance;
    @Getter int viewDistanceSquared;

    public AbstractObservableFakeEntity(final boolean global, final int viewDistance,
                                        final @NonNull Location location) {
        super(location);

        this.global = global;
        this.viewDistance = viewDistance;
        viewDistanceSquared = viewDistance * viewDistance;
    }

    @Override
    public boolean shouldSee(final Player player) {
        final Location thisLocation;
        return player.getWorld() == (thisLocation = location).getWorld()
                && player.getEyeLocation().distanceSquared(thisLocation) <= viewDistanceSquared;
    }
}
