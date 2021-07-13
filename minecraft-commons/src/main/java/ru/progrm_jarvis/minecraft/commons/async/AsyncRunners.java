package ru.progrm_jarvis.minecraft.commons.async;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.minecraft.commons.MinecraftEnvironment;

/**
 * Utilities for easier use of {@link AsyncRunner}s.
 */
@UtilityClass
public class AsyncRunners {

    /**
     * Gets an {@link AsyncRunner} aware of current {@link MinecraftEnvironment}.
     *
     * @param bukkitPlugin Bukkit plugin if there is one to be used for Bukkit async caller
     * @param bungeePlugin BungeeCord plugin if there is one to be used for BungeeCord async caller
     * @return an async caller capable of performing its operations in current environment
     * @throws IllegalStateException if there is no async caller available for current context
     *
     * @implNote This will give a caller of Bukkit or Bungee depending on availability of their classes
     */
    public AsyncRunner getMinecraftEnvironmentAware(final @Nullable Object bukkitPlugin,
                                                    final @Nullable Object bungeePlugin) {
        // try use Bukkit's
        if (MinecraftEnvironment.BUKKIT_API.isAvailable()) attempt: {
            if (bukkitPlugin == null) break attempt;

            return new BukkitSchedulerAsyncRunner((org.bukkit.plugin.Plugin) bukkitPlugin);
        }

        // try use BungeeCord's
        if (MinecraftEnvironment.BUNGEE_API.isAvailable()) attempt: {
            if (bungeePlugin == null) break attempt;

            return new BungeeSchedulerAsyncRunner((net.md_5.bungee.api.plugin.Plugin) bungeePlugin);
        }

        throw new IllegalStateException("No AsyncRunner found for current Minecraft environment "
                + "and specified plugins:" + bukkitPlugin + " [Bukkit], " + bungeePlugin + " [BungeeCord]");
    }
}
