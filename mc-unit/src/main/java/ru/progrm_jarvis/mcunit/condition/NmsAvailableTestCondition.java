package ru.progrm_jarvis.mcunit.condition;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.progrm_jarvis.mcunit.annotation.EnabledIfNms;
import ru.progrm_jarvis.mcunit.util.NmsTestUtil;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;

public final class NmsAvailableTestCondition implements ExecutionCondition {

    private static final @NotNull ConditionEvaluationResult
            ENABLED_BY_DEFAULT = enabled('@' + EnabledIfNms.class.getName() + " is not present"),
            ENABLED_WITH_NMS_AVAILABLE = enabled("Enabled with NMS available"),
            DISABLED_WITH_NMS_UNAVAILABLE = disabled("Disabled with NMS unavailable");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final @NotNull ExtensionContext context) {
        return isAnnotated(context.getElement(), EnabledIfNms.class)
                ? NmsTestUtil.isEnvironmentNms() ? ENABLED_WITH_NMS_AVAILABLE : DISABLED_WITH_NMS_UNAVAILABLE
                : ENABLED_BY_DEFAULT;
    }
}
