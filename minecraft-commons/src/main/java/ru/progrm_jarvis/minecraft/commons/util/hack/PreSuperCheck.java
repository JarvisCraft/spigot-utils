package ru.progrm_jarvis.minecraft.commons.util.hack;

import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * Utility used for letting precondition-checks in constructors before calling super.
 */
@UtilityClass
public class PreSuperCheck {

    /**
     * Performs all specified checks before returning the specified value.
     *
     * @param returned the value which will finally be returned
     * @param checks checks to perform
     * @param <T> type of value returned
     * @return the value specified
     */
    public <T> T beforeSuper(final T returned, final Runnable... checks) {
        for (val check : checks) check.run();

        return returned;
    }
}
