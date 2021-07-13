package ru.progrm_jarvis.minecraft.commons.mapimage;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.progrm_jarvis.javacommons.lazy.Lazy;
import ru.progrm_jarvis.minecraft.commons.util.hack.PreSuperCheck;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImage.blankPixels;
import static ru.progrm_jarvis.minecraft.commons.mapimage.MapImageColor.NO_COLOR_CODE;

/**
 * The default {@link MapImage} implementation which stores its pixels as a 1-dimensional.
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultMapImage implements MapImage {

    /**
     * {@code byte}-array of pixels of an image by X, Y indexes.
     * A pixel can be accessed as {@code pixels[x + y * getWidth()]}
     */
    byte[] pixels;
    @Getter int width, height;
    byte displayMode;

    /**
     * Lazily initialized non-buffered drawer
     */
    Lazy<Drawer> drawer = Lazy.create(Drawer::new);

    /**
     * Lazily initialized buffered drawer
     */
    Lazy<BufferedDrawer> bufferedDrawer = Lazy.create(BufferedDrawer::new);

    /**
     * All subscribers active.
     */
    Collection<Consumer<Delta>> updateSubscribers = new ArrayList<>();

    /**
     * Creates new map image from pixels.
     *
     * @param pixels array of Minecraft color IDs (columns of rows)
     * @param displayMode possible map image display mode (from {@code 0} to {@code 4})
     */
    public DefaultMapImage(final byte[] pixels, final byte displayMode) {
        this(
                PreSuperCheck.beforeSuper(pixels,
                        () -> checkArgument(pixels.length == PIXELS_COUNT, "pixels length should be " + PIXELS_COUNT)
                ),
                WIDTH, HEIGHT,
                PreSuperCheck.beforeSuper(displayMode,
                        () -> checkArgument(
                                displayMode >= 0 && displayMode <= 4, "displayMode should be between 0 and 4"
                        )
                )
        );
    }

    @Override
    public byte getDisplay() {
        return displayMode;
    }

    @Override
    public byte[] getMapData() {
        return pixels;
    }

    @Override
    public byte[] getMapData(final int leastX, final int leastY, final int width, final int height) {
        val data = new byte[width * height];
        var i = 0;
        final int xBound = leastX + width, yBound = leastY + height;
        for (var x = leastX; x < xBound; x++) for (var y = 0; y < yBound; y++) data[i] = pixels[x + y * width];

        return data;
    }

    /**
     * Creates new map image from image.
     *
     * @param image from which to create the map image
     * @param resize whether the image should be resized or cut to fit map image dimensions
     * @param displayMode display mode of the image
     * @return created map image
     */
    public static MapImage from(final @NonNull BufferedImage image, final boolean resize,
                                final byte displayMode) {
        return new DefaultMapImage(MapImages.getMapImagePixels(image, resize), displayMode);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Updates and Subscriptions logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isSubscribable() {
        return true;
    }

    @Override
    public void subscribeOnUpdates(final Consumer<Delta> subscriber) {
        updateSubscribers.add(subscriber);
    }

    @Override
    public void unsubscribeFromUpdates(final Consumer<Delta> subscriber) {
        updateSubscribers.remove(subscriber);
    }

    @Override
    public void onUpdate(final @NonNull Delta delta) {
        for (val updateSubscriber : updateSubscribers) updateSubscriber.accept(delta);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drawers
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public MapImage.Drawer drawer() {
        return drawer.get();
    }

    @Override
    public MapImage.BufferedDrawer bufferedDrawer() {
        return bufferedDrawer.get();
    }

    @ToString
    @EqualsAndHashCode
    protected final class Drawer implements MapImage.Drawer {

        @Override
        public MapImage.Drawer px(final int x, final int y, final byte color) {
            pixels[x + y * getWidth()] = color;

            return this;
        }

        @Override
        public MapImage.Drawer fill(final byte color) {
            Arrays.fill(pixels, color);
            onUpdate(Delta.of(pixels, width, 0, 0));

            return this;
        }
    }

    /**
     * Buffered drawer based on 2-dimensional {@code byte}-array of changed pixels and {@code int}-bounds.
     */
    @Getter
    @ToString
    @EqualsAndHashCode
    @FieldDefaults(level = AccessLevel.PROTECTED)
    protected final class BufferedDrawer implements MapImage.BufferedDrawer {

        /**
         * Array of changed pixels
         */
        final byte[] buffer = blankPixels(new byte[DefaultMapImage.this.getWidth() * DefaultMapImage.this.getHeight()]);

        boolean unchanged = true;

        /**
         * The least X-coordinate of changed image segment.
         */
        int leastChangedX = Delta.NONE,
        /**
         * The least Y-coordinate of changed image segment.
         */
        leastChangedY = Delta.NONE,
        /**
         * The most X-coordinate of changed image segment.
         */
        mostChangedX = Delta.NONE,
        /**
         * The most Y-coordinate of changed image segment.
         */
        mostChangedY = Delta.NONE;

        /**
         * Resets this buffered drawer setting {@link #unchanged} to {@code true} and resetting its buffer.
         */
        private void reset() {
            unchanged = true;
            leastChangedX = leastChangedY = mostChangedX = mostChangedY = Delta.NONE;

            blankPixels(buffer);
        }

        @Override
        public Delta dispose() {
            // real disposal should happen only if there are changes
            val delta = getDelta();

            // perform image update only if delta is not empty (there are changes)
            if (!delta.isEmpty()) {
                final int width = delta.width(), height = delta.height(),
                        leastX = delta.leastX(), leastY = delta.leastY();

                val pixels = delta.pixels();
                var i = -1;
                for (var y = leastY; y < height; y++) {
                    val offset = y * width;
                    for (var x = leastX; x < width; x++)
                        if (pixels[++i] != NO_COLOR_CODE) DefaultMapImage.this
                                .pixels[x + offset] = pixels[i];
                }

                reset();

                onUpdate(delta);
            }

            return delta;
        }

        @Override
        public Delta getDelta() {
            if (unchanged) return Delta.EMPTY;

            val width = mostChangedX - leastChangedX + 1;
            val pixels = new byte[width * (mostChangedY - leastChangedY + 1)];
            var i = 0;
            for (var y = leastChangedY; y < mostChangedY; y++) {
                val offset = y * width;
                for (var x = leastChangedX; x < mostChangedX; x++) pixels[i++] = buffer[x + offset];
            }

            return Delta.of(pixels, width, leastChangedX, leastChangedY);
        }

        ///////////////////////////////////////////////////////////////////////////
        // Drawing
        ///////////////////////////////////////////////////////////////////////////

        @Override
        public MapImage.Drawer px(final int x, final int y, final byte color) {
            // put the changed pixel to the buffer
            buffer[x + y * width] = color;

            // perform delta update if needed
            // if it is the first update then the pixels is the zone of changes
            if (unchanged) {
                unchanged = false;

                leastChangedX = mostChangedX = x;
                leastChangedY = mostChangedY = y;
            } else {
                if (x < leastChangedX) leastChangedX = x;
                else if (x > mostChangedX) mostChangedX = x;

                if (y < leastChangedY) leastChangedY = y;
                else if (y > mostChangedY) mostChangedY = y;
            }

            return this;
        }

        @Override
        public MapImage.Drawer fill(final byte color) {
            unchanged = false;

            Arrays.fill(buffer, color);
            leastChangedX = leastChangedY = 0;
            mostChangedX = WIDTH;
            mostChangedY = HEIGHT;

            return this;
        }
    }
}
