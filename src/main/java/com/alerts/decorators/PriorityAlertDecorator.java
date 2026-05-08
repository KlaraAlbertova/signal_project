package com.alerts.decorators;

import com.alerts.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator {
    private String priorityLevel;

    public PriorityAlertDecorator(Alert decoratedAlert, String priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getCondition() {
        return "[PRIORITY: " + priorityLevel + "] " + decoratedAlert.getCondition();
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }
}
