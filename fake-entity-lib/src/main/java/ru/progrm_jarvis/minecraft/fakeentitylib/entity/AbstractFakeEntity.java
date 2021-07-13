package ru.progrm_jarvis.minecraft.fakeentitylib.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.World;
import ru.progrm_jarvis.javacommons.annotation.DontOverrideEqualsAndHashCode;

/**
 * Base for most common implementations of {@link FakeEntity}.
 */
@DontOverrideEqualsAndHashCode("Entities are mutable and have no real IDs in practise")
@ToString
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractFakeEntity implements FakeEntity {

    final @NonNull Location location;
    @Getter boolean visible = true; // setter should be created manually to perform visualisation logic

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }
}
