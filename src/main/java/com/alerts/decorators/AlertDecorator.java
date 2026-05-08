package com.alerts.decorators;

import com.alerts.alerts.Alert;

public abstract class AlertDecorator extends Alert {
    protected Alert decoratedAlert;

    public AlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert.getPatientId(), decoratedAlert.getCondition(), decoratedAlert.getTimestamp());
        this.decoratedAlert = decoratedAlert;
    }

    public String getPatientId() {
        return decoratedAlert.getPatientId();
    }

    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }
}