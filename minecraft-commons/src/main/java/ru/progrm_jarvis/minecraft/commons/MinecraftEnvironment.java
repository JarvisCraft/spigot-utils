package ru.progrm_jarvis.minecraft.commons;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import ru.progrm_jarvis.minecraft.commons.util.ReflectionUtil;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum MinecraftEnvironment {

    BUKKIT_API("org.bukkit.Bukkit"),
    SPIGOT_API("org.spigotmc.CustomTimingsHandler"),
    NMS(new String[0], "net.minecraft.server"),
    CRAFTBUKKIT(new String[0], "org.bukkit.craftbukkit"),
    BUNGEE_API("net.md_5.bungee.api.ProxyServer"),
    BUNGEECORD("net.md_5.bungee.BungeeCord");

    @NonFinal boolean available;
    @NonNull String[] checkedClasses, checkedPackages;

    MinecraftEnvironment(final @NonNull String[] checkedClasses, final @NonNull String... checkedPackages) {
        this.checkedClasses = checkedClasses;
        this.checkedPackages = checkedPackages;
    }

    MinecraftEnvironment(final String... checkedClasses) {
        this(checkedClasses, new String[0]);
    }

    /**
     * Forcefully sets this environment as an available.
     *
     * @deprecated Should only be called in case the specified environment is available although cannot be recognized.
     */
    @Deprecated
    @Synchronized
    public void setAvailable() {
        available = true;
    }

    @Synchronized
    public boolean isAvailable() {
        return available || (available = classesAvailable() && packagesAvailable());
    }

    private boolean packagesAvailable() {
        if (checkedPackages.length == 0) return true;
        for (val checkedPackage : checkedPackages) if (Package.getPackage(checkedPackage) == null) return false;
        return true;
    }

    private boolean classesAvailable() {
        if (checkedClasses.length == 0) return true;
        for (val checkedClass : checkedClasses) if (!ReflectionUtil.isClassAvailable(checkedClass)) return false;
        return true;
    }
}
