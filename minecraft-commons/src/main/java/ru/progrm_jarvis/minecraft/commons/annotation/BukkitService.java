package ru.progrm_jarvis.minecraft.commons.annotation;

import java.lang.annotation.*;

/**
 * Marker to indicate that the annotated element is normally registered as a Bukkit service.
 *
 * @see org.bukkit.plugin.ServicesManager#getRegistration(Class)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface BukkitService {
}
