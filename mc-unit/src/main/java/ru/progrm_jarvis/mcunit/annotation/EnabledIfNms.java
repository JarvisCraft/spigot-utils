package ru.progrm_jarvis.mcunit.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import ru.progrm_jarvis.mcunit.condition.NmsAvailableTestCondition;

import java.lang.annotation.*;

/**
 * Indicates that the test should only be run if current environment has NMS-classes.
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(NmsAvailableTestCondition.class)
public @interface EnabledIfNms {}
