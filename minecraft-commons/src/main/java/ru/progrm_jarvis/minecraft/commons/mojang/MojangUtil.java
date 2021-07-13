package ru.progrm_jarvis.minecraft.commons.mojang;

import com.mojang.authlib.GameProfile;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.UUID;

/**
 * A pack of Utilities related to Mojang API specific stuff.
 */
@UtilityClass
public class MojangUtil {

    /**
     * Converts a UUID in a form returned by Mojang API calls (no dashes) to a {@link UUID} object.
     *
     * @param mojangUuid UUID in a non-standard form returned by Mojang API call (no dashes) to convert
     * @return standard UUID object, result of conversion
     */
    public UUID fromMojangUuid(final @NonNull String mojangUuid) {
        return UUID.fromString(
                mojangUuid.substring(0, 8)
                        + '-' + mojangUuid.substring(8, 12)
                        + '-' + mojangUuid.substring(12, 16)
                        + '-' + mojangUuid.substring(16, 20)
                        + '-' + mojangUuid.substring(20, 32)
        );
    }

    /**
     * Converts a {@link UUID} object to a form used by Mojang API (no dashes).
     *
     * @param uuid standard UUID object to convert
     * @return UUID in a non-standard form used by Mojang API (no dashes), result of conversion
     */
    public String toMojangUuid(final @NonNull UUID uuid) {
        val stringUuid = uuid.toString();
        return stringUuid.substring(0, 8)
                + stringUuid.substring(9, 13)
                + stringUuid.substring(14, 18)
                + stringUuid.substring(19, 23)
                + stringUuid.substring(24, 36);
    }

    /**
     * Checks whether the specified profile has all properties signed.
     *
     * @param profile profile to check
     * @return {@code true} if the specified profile has all its properties signed
     * or {@code false} if any of those is unsigned
     */
    public boolean isSigned(final @NonNull GameProfile profile) {
        for (val entry : profile.getProperties().entries()) if (!entry.getValue().hasSignature()) return false;
        return true;
    }
}
