package ru.progrm_jarvis.minecraft.commons.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.function.*;

/**
 * Utility for comfortable functional retrieval of system properties.
 */
@UtilityClass
public class SystemPropertyUtil {

    /**
     * Gets the system property by the name specified applying needed transformations to it
     * and using the default value specified if the property is unset.
     *
     * @param propertyName name of the property
     * @param transformer transformer of a {@link String} property value to the required type.
     * @param defaultValueSupplier supplier of the default value (called only if needed) to use if the property is unset
     * @param <T> type of the resulting property value
     * @return found property transformed to required type or the default value if the property is unset
     */
    public <T> T getSystemProperty(final @NonNull String propertyName,
                                   final @NonNull Function<String, T> transformer,
                                   final @NonNull Supplier<T> defaultValueSupplier) {
        val property = System.getProperty(propertyName);

        return property == null ? defaultValueSupplier.get() : transformer.apply(property);
    }

    /**
     * Gets the system property by the name specified applying needed transformations to it
     * and using the default value specified if the property is unset.
     *
     * @param propertyName name of the property
     * @param transformer transformer of a {@link String} property value to the required type.
     * @param defaultValue default value to use if the property is unset
     * @param <T> type of the resulting property value
     * @return found property transformed to required type or the default value if the property is unset
     */
    public <T> T getSystemProperty(final @NonNull String propertyName,
                                   final @NonNull Function<String, T> transformer,
                                   final @NonNull T defaultValue) {
        return getSystemProperty(propertyName, transformer, (Supplier<T>) () -> defaultValue);
    }

    /**
     * Gets the system property of type {@code boolean} by the name specified
     * and using the default value specified if the property is unset.
     *
     * @param propertyName name of the property
     * @param defaultValueSupplier supplier of the default value (called only if needed) to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset
     */
    public boolean getSystemPropertyBoolean(final @NonNull String propertyName,
                                            final @NonNull BooleanSupplier defaultValueSupplier) {
        val property = System.getProperty(propertyName);

        return property == null ? defaultValueSupplier.getAsBoolean() : Boolean.parseBoolean(property);
    }

    /**
     * Gets the system property of type {@code boolean} by the name specified
     * and using the default value specified if the property is unset.
     *
     * @param propertyName name of the property
     * @param defaultValue default value to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset
     */
    public boolean getSystemPropertyBoolean(final @NonNull String propertyName, final boolean defaultValue) {
        return getSystemPropertyBoolean(propertyName, () -> defaultValue);
    }

    /**
     * Gets the system property of type {@code int} by the name specified
     * and using the default value specified if the property is unset or is not a valid number.
     *
     * @param propertyName name of the property
     * @param defaultValueSupplier supplier of the default value (called only if needed) to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset or is not a valid number
     */
    public int getSystemPropertyInt(final @NonNull String propertyName,
                                    final @NonNull IntSupplier defaultValueSupplier) {
        val property = System.getProperty(propertyName);

        if (property == null) return defaultValueSupplier.getAsInt();
        try {
            return Integer.parseInt(property);
        } catch (final NumberFormatException e) {
            return defaultValueSupplier.getAsInt();
        }
    }

    /**
     * Gets the system property of type {@code int} by the name specified
     * and using the default value specified if the property is unset or is not a valid number.
     *
     * @param propertyName name of the property
     * @param defaultValue default value to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset or is not a valid number
     */
    public int getSystemPropertyInt(final @NonNull String propertyName, final int defaultValue) {
        return getSystemPropertyInt(propertyName, () -> defaultValue);
    }

    /**
     * Gets the system property of type {@code long} by the name specified
     * and using the default value specified if the property is unset or is not a valid number.
     *
     * @param propertyName name of the property
     * @param defaultValueSupplier supplier of the default value (called only if needed) to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset or is not a valid number
     */
    public long getSystemPropertyLong(final @NonNull String propertyName,
                                      final @NonNull LongSupplier defaultValueSupplier) {
        val property = System.getProperty(propertyName);

        if (property == null) return defaultValueSupplier.getAsLong();
        try {
            return Long.parseLong(property);
        } catch (final NumberFormatException e) {
            return defaultValueSupplier.getAsLong();
        }
    }

    /**
     * Gets the system property of type {@code long} by the name specified
     * and using the default value specified if the property is unset or is not a valid number.
     *
     * @param propertyName name of the property
     * @param defaultValue default value to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset or is not a valid number
     */
    public long getSystemPropertyLong(final @NonNull String propertyName, final long defaultValue) {
        return getSystemPropertyLong(propertyName, () -> defaultValue);
    }

    /**
     * Gets the system property of type {@code double} by the name specified
     * and using the default value specified if the property is unset or is not a valid number.
     *
     * @param propertyName name of the property
     * @param defaultValueSupplier supplier of the default value (called only if needed) to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset or is not a valid number
     */
    public double getSystemPropertyDouble(final @NonNull String propertyName,
                                          final @NonNull DoubleSupplier defaultValueSupplier) {
        val property = System.getProperty(propertyName);

        if (property == null) return defaultValueSupplier.getAsDouble();
        try {
            return Double.parseDouble(property);
        } catch (final NumberFormatException e) {
            return defaultValueSupplier.getAsDouble();
        }
    }

    /**
     * Gets the system property of type {@code double} by the name specified
     * and using the default value specified if the property is unset or is not a valid number.
     *
     * @param propertyName name of the property
     * @param defaultValue default value to use if the property is unset
     * @return found property transformed to required type
     * or the default value if the property is unset or is not a valid number
     */
    public double getSystemPropertyDouble(final @NonNull String propertyName, final double defaultValue) {
        return getSystemPropertyDouble(propertyName, () -> defaultValue);
    }
}
