package ru.progrm_jarvis.minecraft.commons.plugin;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * An object which contains a bungeecord plugin instance.
 */
public interface BungeePluginContainer {

    /**
     * Gets the bungeecord plugin contained by this object.
     *
     * @return bungeecord plugin of this object
     */
    Plugin getBungeePlugin();
}
