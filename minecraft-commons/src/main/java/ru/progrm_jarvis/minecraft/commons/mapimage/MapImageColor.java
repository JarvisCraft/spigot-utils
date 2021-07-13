package ru.progrm_jarvis.minecraft.commons.mapimage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.bukkit.map.MapPalette;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.minecraft.commons.util.BitwiseUtil;
import ru.progrm_jarvis.minecraft.commons.util.SystemPropertyUtil;
import ru.progrm_jarvis.minecraft.commons.util.image.ColorUtil;

import java.awt.*;

import static java.lang.Math.abs;

/**
 * A cached color which provides easy conversions between full 24-bit RGB and Minecraft Map colors.
 *
 * @apiNote Minecraft maps don't allow alpha channel
 */
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MapImageColor {

    /**
     * A primitive constant value to use when there is no color code.
     */
    public static final byte NO_COLOR_CODE = 0;

    private static final Object COLOR_IDS_CACHE_MUTEX = new Object[0];

    private static final Cache<Integer, MapImageColor> COLOR_CACHE = CacheBuilder.newBuilder()
            .weakValues()
            .concurrencyLevel(SystemPropertyUtil.getSystemPropertyInt(
                    MapImageColor.class.getCanonicalName() + ".color-cache-concurrency-level", 2
            ))
            .build();

    /**
     * All associations of color's with their available IDs.
     */
    private static final Object2ByteMap<MapImageColor> COLOR_CODE_CACHE = new Object2ByteOpenHashMap<>();

    /**
     * 8 bits describing red part of the color
     */
    byte red,
    /**
     * 8 bits describing green part of the color
     */
    green,
    /**
     * 8 bits describing blue part of the color
     */
    blue;

    /**
     * An {@code int} representation of the color. Also used as the only field for hash-code generation.
     */
    @EqualsAndHashCode.Include int rgb;

    ///////////////////////////////////////////////////////////////////////////
    // Construction
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a new map image color instance based on 3 base colors.
     * This is an internal constructor as, normally, there should only exist one cached instance of any used color.
     *
     * @param red red color channel
     * @param green green color channel
     * @param blue blue color channel
     */
    private MapImageColor(final byte red, final byte green, final byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

        this.rgb = ColorUtil.toArgb(red, green, blue);
    }

    /**
     * Constructs a new map image color instance based on 3 base colors.
     * This is an internal constructor as, normally, there should only exist one cached instance of any used color.
     *
     * @param red red color channel
     * @param green green color channel
     * @param blue blue color channel
     */
    private MapImageColor(final int red, final int green, final int blue) {
        this((byte) red, (byte) green, (byte) blue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Conversions
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Creates new map image color from specified {@link java.awt} {@link Color}.
     *
     * @param color color to convert to map image color object
     * @return map image color equivalent of specified color object
     */
    public static @NotNull MapImageColor from(final @NonNull Color color) {
        return of(color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Gets or creates cached map image color from specified {@code int}-RGB.
     *
     * @param rgb RGB encoded as {@code int}
     * @return cached or created and cached map image color
     */
    @SneakyThrows
    public static @NotNull MapImageColor of(final int rgb) {
        return COLOR_CACHE.get(rgb, () -> new MapImageColor(ColorUtil.red(rgb), ColorUtil.green(rgb), ColorUtil.blue(rgb)));
    }

    /**
     * Gets or creates cached map image color from specified color divided on color channels.
     *
     * @param red red color channel
     * @param green green color channel
     * @param blue blue color channel
     * @return cached or created and cached map image color
     */
    @SneakyThrows
    @NotNull public static MapImageColor of(final byte red, final byte green, final byte blue) {
        return COLOR_CACHE.get(ColorUtil.toArgb(red, green, blue), () -> new MapImageColor(red, green, blue));
    }

    /**
     * Gets or creates cached map image color from specified color divided on color channels.
     *
     * @param red red color channel
     * @param green green color channel
     * @param blue blue color channel
     * @return cached or created and cached map image color
     *
     * @apiNote alias {@link #of(byte, byte, byte)} using {@code int}s not to perform casts in method call
     */
    public static @NotNull MapImageColor of(final int red, final int green, final int blue) {
        return of((byte) red, (byte) green, (byte) blue);
    }

    /**
     * Gets the id of the color closest to the one given. This value is cached for further usage.
     *
     * @param color color for which to find the closest available color code
     * @return closest available color code
     *
     * @implNote uses {@link MapPalette#matchColor(Color)} because (although it is deprecated) it is the simplest way
     */
    @SneakyThrows
    public static byte getClosestColorCode(final MapImageColor color) {
        if (COLOR_CODE_CACHE.containsKey(color)) return COLOR_CODE_CACHE.get(color);

        // the value which will store the color code
        final byte colorCode;
        synchronized (COLOR_IDS_CACHE_MUTEX) {
            //noinspection deprecation ( use of MapPalette#matchColor(..)
            COLOR_CODE_CACHE.put(color, colorCode = MapPalette.matchColor(new Color(color.rgb)));
        }

        return colorCode;
    }

    /**
     * Gets the id of the color closest to the one given by calculating
     * dissimilarity rate of each available with the one given.
     * This value is cached for further usage.
     *
     * @param rgb RGB-color {@code int} for which to find the closest available color code
     * @return closest available color id
     */
    @SneakyThrows
    public static byte getClosestColorCode(final int rgb) {
        return getClosestColorCode(of(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Difference counting
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Distance squared (as if colors were 3 axises)
    ///////////////////////////////////////////////////////////////////////////

    public static int getDistanceSquared(final int red1, final int green1, final int blue1,
                                         final int red2, final int green2, final int blue2) {
        final int dRed = red2 - red1, dGreen = green2 - green1, dBlue = blue2 - blue1;

        return dRed * dRed + dBlue * dBlue + dGreen * dGreen;
    }

    public static int getDistanceSquared(final byte red1, final byte green1, final byte blue1,
                                         final byte red2, final byte green2, final byte blue2) {
        val dRed = BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1);
        val dGreen = BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1);
        val dBlue = BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1);

        return getDistanceSquared(
                BitwiseUtil.byteToUnsignedInt(red1),
                BitwiseUtil.byteToUnsignedInt(green1),
                BitwiseUtil.byteToUnsignedInt(blue1),
                BitwiseUtil.byteToUnsignedInt(red2),
                BitwiseUtil.byteToUnsignedInt(green2),
                BitwiseUtil.byteToUnsignedInt(blue2)
        );
    }

    public static int getDistanceSquared(final @NonNull MapImageColor color1, final @NonNull MapImageColor color2) {
        return getDistanceSquared(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getDistanceSquared(final int rgb1, final int rgb2) {
        return getDistanceSquared(
                ColorUtil.red(rgb1), ColorUtil.green(rgb1), ColorUtil.blue(rgb1),
                ColorUtil.red(rgb2), ColorUtil.green(rgb2), ColorUtil.blue(rgb2)
        );
    }

    public int getDistanceSquared(final byte red, final byte green, final byte blue) {
        return getDistanceSquared(this.red, this.green, this.blue, red, green, blue);
    }

    public int getDistanceSquared(final int red, final int green, final int blue) {
        return getDistanceSquared(
                BitwiseUtil.byteToUnsignedInt(this.red),
                BitwiseUtil.byteToUnsignedInt(this.green),
                BitwiseUtil.byteToUnsignedInt(this.blue),
                red, green, blue);
    }

    public int getDistanceSquared(final @NonNull MapImageColor other) {
        return getDistanceSquared(red, green, blue, other.red, other.green, other.blue);
    }

    public int getDistanceSquared(final int rgb) {
        return getDistanceSquared(ColorUtil.red(rgb), ColorUtil.green(rgb), ColorUtil.blue(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sum of channels
    ///////////////////////////////////////////////////////////////////////////

    public static int getSum(final int red1, final int green1, final int blue1,
                             final int red2, final int green2, final int blue2) {
        return abs(red2 - red1) + abs(green2 - green1) + abs(blue2 - blue1);
    }

    public static int getSum(final byte red1, final byte green1, final byte blue1,
                             final byte red2, final byte green2, final byte blue2) {
        return getSum(
                BitwiseUtil.byteToUnsignedInt(red1),
                BitwiseUtil.byteToUnsignedInt(green1),
                BitwiseUtil.byteToUnsignedInt(blue1),
                BitwiseUtil.byteToUnsignedInt(red2),
                BitwiseUtil.byteToUnsignedInt(green2),
                BitwiseUtil.byteToUnsignedInt(blue2)
        );
    }

    public static int getSum(final @NonNull MapImageColor color1, final @NonNull MapImageColor color2) {
        return getSum(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getSum(final int rgb1, final int rgb2) {
        return getSum(
                ColorUtil.red(rgb1), ColorUtil.green(rgb1), ColorUtil.blue(rgb1),
                ColorUtil.red(rgb2), ColorUtil.green(rgb2), ColorUtil.blue(rgb2)
        );
    }

    public int getSum(final byte red, final byte green, final byte blue) {
        return getSum(this.red, this.green, this.blue, red, green, blue);
    }

    public int getSum(final int red, final int green, final int blue) {
        return getSum(
                BitwiseUtil.byteToUnsignedInt(this.red),
                BitwiseUtil.byteToUnsignedInt(this.green),
                BitwiseUtil.byteToUnsignedInt(this.blue),
                red, green, blue
        );
    }

    public int getSum(final @NonNull MapImageColor other) {
        return getSum(red, green, blue, other.red, other.green, other.blue);
    }

    public int getSum(final int rgb) {
        return getSum(ColorUtil.red(rgb), ColorUtil.green(rgb), ColorUtil.blue(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Multiplication product of channels
    ///////////////////////////////////////////////////////////////////////////

    public static int getMultiplicationProduct(final int red1, final int green1, final int blue1,
                                               final int red2, final int green2, final int blue2) {
        return (red2 - red1) * (green2 - green1) * (blue2 - blue1);
    }

    public static int getMultiplicationProduct(final byte red1, final byte green1, final byte blue1,
                                               final byte red2, final byte green2, final byte blue2) {
        return (BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1))
                * (BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1))
                * (BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1));
    }

    public static int getMultiplicationProduct(final @NonNull MapImageColor color1, final @NonNull MapImageColor color2) {
        return getMultiplicationProduct(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getMultiplicationProduct(final int rgb1, final int rgb2) {
        return getMultiplicationProduct(
                ColorUtil.red(rgb1), ColorUtil.green(rgb1), ColorUtil.blue(rgb1),
                ColorUtil.red(rgb2), ColorUtil.green(rgb2), ColorUtil.blue(rgb2)
        );
    }

    public int getMultiplicationProduct(final int red, final int green, final int blue) {
        return getMultiplicationProduct(
                BitwiseUtil.byteToUnsignedInt(this.red),
                BitwiseUtil.byteToUnsignedInt(this.green),
                BitwiseUtil.byteToUnsignedInt(this.blue),
                red, green, blue
        );
    }

    public int getMultiplicationProduct(final byte red, final byte green, final byte blue) {
        return getMultiplicationProduct(this.red, this.green, this.blue, red, green, blue);
    }

    public int getMultiplicationProduct(final @NonNull MapImageColor other) {
        return getMultiplicationProduct(red, green, blue, other.red, other.green, other.blue);
    }

    public int getMultiplicationProduct(final int rgb) {
        return getMultiplicationProduct(ColorUtil.red(rgb), ColorUtil.green(rgb), ColorUtil.blue(rgb));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Natural distance
    // Due to nature of human's eyes it is more accurate to use
    // unequal coefficients for color-channels to be more accurate
    //   (red) = 0.3
    // (green) = 0.59
    //  (blue) = 0.11
    ///////////////////////////////////////////////////////////////////////////

    public static int getNaturalDistanceSquared(final byte red1, final byte green1, final byte blue1,
                                                final byte red2, final byte green2, final byte blue2) {
        val dRed = (BitwiseUtil.byteToUnsignedInt(red2) - BitwiseUtil.byteToUnsignedInt(red1)) * 0.3;
        val dGreen = (BitwiseUtil.byteToUnsignedInt(green2) - BitwiseUtil.byteToUnsignedInt(green1)) * 0.59;
        val dBlue = (BitwiseUtil.byteToUnsignedInt(blue2) - BitwiseUtil.byteToUnsignedInt(blue1)) * 0.11;

        return (int) (dRed * dRed + dBlue * dBlue + dGreen * dGreen);
    }

    public static int getNaturalDistanceSquared(final int red1, final int green1, final int blue1,
                                                final int red2, final int green2, final int blue2) {
        final double dRed = (red2 - red1) * 0.3, dGreen = (green2 - green1) * 0.59, dBlue = (blue2 - blue1) * 0.11;

        return (int) (dRed * dRed + dBlue * dBlue + dGreen * dGreen);
    }

    public static int getNaturalDistanceSquared(final @NonNull MapImageColor color1, final @NonNull MapImageColor color2) {
        return getNaturalDistanceSquared(color1.red, color1.green, color1.blue, color2.red, color2.green, color2.blue);
    }

    public static int getNaturalDistanceSquared(final int rgb1, final int rgb2) {
        return getNaturalDistanceSquared(
                ColorUtil.red(rgb1), ColorUtil.green(rgb1), ColorUtil.blue(rgb1),
                ColorUtil.red(rgb2), ColorUtil.green(rgb2), ColorUtil.blue(rgb2)
        );
    }

    public int getNaturalDistanceSquared(final int red, final int green, final int blue) {
        return getNaturalDistanceSquared(
                BitwiseUtil.byteToUnsignedInt(this.red),
                BitwiseUtil.byteToUnsignedInt(this.green),
                BitwiseUtil.byteToUnsignedInt(this.blue),
                red, green, blue);
    }

    public int getNaturalDistanceSquared(final byte red, final byte green, final byte blue) {
        return getNaturalDistanceSquared(this.red, this.green, this.blue, red, green, blue);
    }

    public int getNaturalDistanceSquared(final @NonNull MapImageColor other) {
        return getNaturalDistanceSquared(red, green, blue, other.red, other.green, other.blue);
    }

    public int getNaturalDistanceSquared(final int rgb) {
        return getNaturalDistanceSquared(ColorUtil.red(rgb), ColorUtil.green(rgb), ColorUtil.blue(rgb));
    }
}
