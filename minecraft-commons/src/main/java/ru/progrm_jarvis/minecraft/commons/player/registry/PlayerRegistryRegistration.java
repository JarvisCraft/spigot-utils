package ru.progrm_jarvis.minecraft.commons.player.registry;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

/**
 * Marker to indicate how the object handles registration in {@link PlayerRegistry}.
 *
 * @apiNote When applied to constructor it indicates the policy for this exact constructor. When applied
 * to the class itself it indicates the default policy for all constructors except for explicitly annotated.
 */
@Inherited
@Documented
@Target({TYPE, CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
public @interface PlayerRegistryRegistration {

    /**
     * Type of object registration policy used by it.
     *
     * @return registration policy for the object
     *
     * @apiNote default value is not provided to force explicit specification
     */
    Policy value();

    /**
     * The way registration in {@link PlayerRegistry} happens for this object.
     */
    enum Policy {

        /**
         * Object registers itself automatically and so is not expected to be registered manually.
         *
         * @apiNote if the object accepts {@link PlayerRegistry} in constructor which will be used for registration
         * then it is also considered as an automatic registration.
         */
        AUTO,

        /**
         * The user of the object should register the object manually.
         */
        MANUAL,

        /**
         * The object doesn't register itself and the user of the object is not forces to.
         */
        OPTIONAL,

        /**
         * The object should not be registered.
         */
        NEVER
    }
}
