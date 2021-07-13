package ru.progrm_jarvis.minecraft.commons.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility containing stuff not available in Reflector yet.
 */
@UtilityClass
public class ReflectionUtil {

    public boolean isClassAvailable(final @NonNull String className) {
        try {
            Class.forName(className);

            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }
}
