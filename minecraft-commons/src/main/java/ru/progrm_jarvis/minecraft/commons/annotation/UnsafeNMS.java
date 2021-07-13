package ru.progrm_jarvis.minecraft.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker to indicate that the annotated element may use unsafe {@code net.minecraft.server} functionality.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface UnsafeNMS {
}
