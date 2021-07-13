package ru.progrm_jarvis.minecraft.commons.plugin;

import org.bukkit.plugin.Plugin;

/**
 * An object which contains a bukkit plugin instance.
 */
public interface BukkitPluginContainer {

    /**
     * Gets the bukkit plugin contained by this object.
     *
     * @return bukkit plugin of this object
     */
    Plugin getBukkitPlugin();
}
