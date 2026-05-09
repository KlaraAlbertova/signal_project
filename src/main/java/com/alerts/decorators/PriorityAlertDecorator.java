package com.alerts.decorators;

import com.alerts.alerts.Alert;

/**
 * Decorator that adds a priority tag to an alert's condition message.
 *
 * <p>Prepends a {@code [PRIORITY: <level>]} label to the wrapped alert's
 * condition to indicate that it requires urgent attention.</p>
 */
public class PriorityAlertDecorator extends AlertDecorator {
    private String priorityLevel;

    /**
     * Constructs a PriorityAlertDecorator with the specified priority level.
     *
     * @param decoratedAlert the alert to wrap
     * @param priorityLevel  the priority label to added
     */
    public PriorityAlertDecorator(Alert decoratedAlert, String priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    /**
     * Returns the condition with a priority tag added.
     *
     * @return the prioritized condition string
     */
    @Override
    public String getCondition() {
        return "[PRIORITY: " + priorityLevel + "] " + decoratedAlert.getCondition();
    }

    /**
     * Returns the priority level of this decorator.
     *
     * @return the priority level string
     */
    public String getPriorityLevel() {
        return priorityLevel;
    }
}
