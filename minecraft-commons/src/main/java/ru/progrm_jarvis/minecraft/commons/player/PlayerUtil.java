package ru.progrm_jarvis.minecraft.commons.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.progrm_jarvis.javacommons.object.ObjectUtil;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class PlayerUtil {

    private final String NAMES_CACHE_CONCURRENCY_LEVEL_PROPERTY_NAME
                = PlayerUtil.class.getCanonicalName() + ".FIELDS_CACHE_CONCURRENCY_LEVEL";

    @NonNull private final Cache<@NotNull UUID, @Nullable String> NAMES_CACHE = CacheBuilder.newBuilder()
            .softValues()
            .concurrencyLevel(SystemPropertyUtil.getSystemPropertyInt(NAMES_CACHE_CONCURRENCY_LEVEL_PROPERTY_NAME, 2))
            .build();

    /**
     * Gets online players as a collection of exact {@link Player} type.
     *
     * @return a view of currently online players cast to exact type
     */
    @SuppressWarnings("unchecked")
    public Collection<Player> getOnlinePlayers() {
        return (Collection<Player>) Bukkit.getOnlinePlayers();
    }

    public Collection<Player> playersAround(final @NonNull Location location, final double radius) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getLocation().distance(location) <= radius)
                .collect(Collectors.toList());
    }

    public Collection<Player> playerEyesAround(final @NonNull Location location, final double radius) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getEyeLocation().distance(location) <= radius)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public static Optional<String> getPlayerName(final @NonNull UUID uuid) {
        return Optional.ofNullable(NAMES_CACHE.get(
                uuid, () -> ObjectUtil.mapOnlyNonNull(OfflinePlayer::getName, Bukkit.getOfflinePlayer(uuid))
        ));
    }
}
