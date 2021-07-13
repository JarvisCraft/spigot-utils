package ru.progrm_jarvis.minecraft.commons.annotation;

import java.lang.annotation.*;

/**
 * A marker indicating that the method annotated should be used asynchronously.
 * This commonly means that this method may perform time-consuming operations.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AsyncExpected {

    /**
     * Indicates which method to use instead as an alternative allowing synchronous calls.
     *
     * @return synchronous alternative
     */
    String value() default "";
}
