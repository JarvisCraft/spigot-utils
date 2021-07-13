package ru.progrm_jarvis.minecraft.commons.util.image;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.Contract;

import java.awt.image.BufferedImage;

import static java.lang.Math.min;

/**
 * Utility for {@link java.awt} related image stuff.
 */
@UtilityClass
public class ImageUtil {

    /**
     * Clones the BufferedImage into a new one.
     *
     * @param image image to clone
     * @return cloned image
     */
    public BufferedImage clone(final @NonNull BufferedImage image) {
        val clonedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        val graphics = clonedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);

        return clonedImage;
    }

    /**
     * Merge multiple images into the first one respecting alpha channel.
     *
     * @param background image to which to merge the foregrounds
     * @param foregrounds images to merge to the foreground
     * @return the given background with foregrounds merged
     *
     * @see #clone(BufferedImage) should be used to keep your source image unmodified
     */
    @Contract("null, _ -> fail; _, null -> fail; !null, _ -> param1")
    public BufferedImage merge(final @NonNull BufferedImage background, final @NonNull BufferedImage... foregrounds) {
        final int width = background.getWidth(), height = background.getHeight();
        val graphics = background.getGraphics();

        for (val foreground : foregrounds) graphics.drawImage(
                foreground.getSubimage(0, 0, min(width, foreground.getWidth()), min(height, foreground.getHeight())),
                0, 0, null
        );

        return background;
    }

    /**
     * Merge multiple images using into the first one using {@link ColorUtil#blendColors(int, int)} .
     *
     * @param background image to which to merge the foregrounds
     * @param foregrounds images to merge to the foreground
     * @return the given background with foregrounds merged
     *
     * @implNote current implementation is based on pixel-by-pixel operation
     *
     * @see #clone(BufferedImage) should be used to keep your source image unmodified
     * @see ColorUtil#blendColors(int, int) used color blending algorithm
     */
    @Contract("null, _ -> fail; _, null -> fail; !null, _ -> param1")
    public BufferedImage mergeSharp(final @NonNull BufferedImage background,
                                    final @NonNull BufferedImage... foregrounds) {
        final int width = background.getWidth(), height = background.getHeight();

        for (val foreground : foregrounds) {
            final int foregroundWidth = min(width, foreground.getWidth()),
                    foregroundHeight = min(height, foreground.getHeight());

            for (var x = 0; x < foregroundWidth; x++) for (var y = 0; y < foregroundHeight; y++) background
                    .setRGB(x, y, ColorUtil.blendColors(background.getRGB(x, y), foreground.getRGB(x, y)));
        }

        return background;
    }
}
