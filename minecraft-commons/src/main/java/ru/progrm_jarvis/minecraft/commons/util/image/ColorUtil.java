package ru.progrm_jarvis.minecraft.commons.util.image;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.Contract;

import static java.lang.Math.min;

/**
 * Utilities related to standard {@code int}-ARGB representation of colors.
 */
@UtilityClass
public class ColorUtil {

    /**
     * Length of bitwise {@code int} shift for accessing alpha channel value of standard {@code int}-ARGB.
     */
    public static final byte ALPHA_CHANNEL = 24,
    /**
     * Length of bitwise {@code int} shift for accessing red color channel value of standard {@code int}-ARGB.
     */
    RED_CHANNEL = 16,
    /**
     * Length of bitwise {@code int} shift for accessing green color channel value of standard {@code int}-ARGB.
     */
    GREEN_CHANNEL = 8,
    /**
     * Length of bitwise {@code int} shift for accessing blue color channel value of standard {@code int}-ARGB.
     */
    BLUE_CHANNEL = 0;

    /**
     * Gets the alpha channel value for the specified ARGB {@code int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return alpha channel value
     */
    @Contract(pure = true)
    public static int alpha(final int rgb) {
        return rgb >> ALPHA_CHANNEL & 0xFF;
    }

    /**
     * Gets the red color channel value for the specified ARGB {@code int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return red color channel value
     */
    @Contract(pure = true)
    public static int red(final int rgb) {
        return rgb >> RED_CHANNEL & 0xFF;
    }

    /**
     * Gets the green color channel value for the specified ARGB {@code int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return green color channel value
     */
    @Contract(pure = true)
    public static int green(final int rgb) {
        return rgb >> GREEN_CHANNEL & 0xFF;
    }

    /**
     * Gets the blue color channel value for the specified ARGB {@code int}.
     *
     * @param rgb RGB encoded as a single integer
     * @return blue color channel value
     */
    @Contract(pure = true)
    public static int blue(final int rgb) {
        return rgb >> BLUE_CHANNEL & 0xFF;
    }

    /**
     * Transforms 4 ARGB channels to a single {@code int}.
     *
     * @param alpha alpha color channel (between {@code 0} and {@code 255})
     * @param red red color channel (between {@code 0} and {@code 255})
     * @param green green color channel (between {@code 0} and {@code 255})
     * @param blue blue color channel (between {@code 0} and {@code 255})
     * @return color as a single RGB {@code int}
     */
    @Contract(pure = true)
    public static int toArgb(final int alpha, final int red, final int green, final int blue) {
        return (alpha << ALPHA_CHANNEL) | (red << RED_CHANNEL) | (green << GREEN_CHANNEL) | (blue << BLUE_CHANNEL);
    }

    /**
     * Transforms 3 color channels channels to a single ARGB {@code int} with no transparency.
     *
     * @param red red color channel (between {@code 0} and {@code 255})
     * @param green green color channel (between {@code 0} and {@code 255})
     * @param blue blue color channel (between {@code 0} and {@code 255})
     * @return color as a single RGB {@code int}
     */
    @Contract(pure = true)
    public static int toArgb(final int red, final int green, final int blue) {
        return toArgb(0xFF, red, green, blue);
    }

    public int blendColorsNoAlpha(final int backgroundColor, final int foregroundColor) {
        return toArgb(
                (red(foregroundColor) + red(backgroundColor)) / 2,
                (green(foregroundColor) + green(backgroundColor)) / 2,
                (blue(foregroundColor) + blue(backgroundColor)) / 2
        );
    }

    /**
     * Blends two ARGB colors into one respecting alphas.
     * @param backgroundColor background color as a standard {@code int}-ARGB
     * @param foregroundColor foreground color as a standard {@code int}-ARGB
     * @return standard {@code int}-ARGB color being the result of color-bleeding
     */
    @Contract(pure = true)
    public int blendColors(final int backgroundColor, final int foregroundColor) {
        val foregroundAlpha = alpha(foregroundColor);
        if (foregroundAlpha == 0x0) return backgroundColor;
        if (foregroundAlpha == 0xFF) return foregroundColor;

        val backgroundAlpha = alpha(backgroundColor);
        if (backgroundAlpha == foregroundAlpha) return foregroundAlpha
                | blendColorsNoAlpha(backgroundColor, foregroundColor) & 0xFFFFFF;

        return toArgb(
                min(backgroundAlpha + foregroundAlpha, 255),
                min((red(backgroundColor) * backgroundAlpha + red(foregroundColor) * foregroundAlpha) / 255, 255),
                min((green(backgroundColor) * backgroundAlpha + green(foregroundColor) * foregroundAlpha) / 255, 255),
                min((blue(backgroundColor) * backgroundAlpha + blue(foregroundColor) * foregroundAlpha) / 255, 255)
        );
    }
}
