package ru.progrm_jarvis.minecraft.commons.util.time;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nonnegative;
import java.util.concurrent.TimeUnit;

/**
 * A simple value consisting of {@code long} duration and its {@link TimeUnit} type.
 */
@Value
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Time {

    public Time(final long duration, final @NonNull TimeUnit unit) {
        Preconditions.checkArgument(duration >= 0, "duration should be non-negative");

        this.duration = duration;
        this.unit = unit;
    }

    /**
     * Duration value
     */
    @Nonnegative long duration;

    /**
     * Unit type
     */
    @NonNull TimeUnit unit;
}
