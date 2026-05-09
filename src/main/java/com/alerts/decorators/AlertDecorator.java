package com.alerts.decorators;

import com.alerts.alerts.Alert;

/**
 * Base decorator class for {@link Alert} objects.
 *
 * <p>Wraps an existing alert and delegates all calls to it by default,
 * allowing subclasses to extend or modify alert behaviour without
 * changing the original alert.</p>
 */
public class AlertDecorator extends Alert {
    protected Alert decoratedAlert;

    /**
     * Constructs an AlertDecorator wrapping the given alert.
     *
     * @param decoratedAlert the alert to wrap
     */
    public AlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert.getPatientId(), decoratedAlert.getCondition(), decoratedAlert.getTimestamp());
        this.decoratedAlert = decoratedAlert;
    }

    /**
     * Returns the patient ID of the wrapped alert.
     *
     * @return the patient ID
     */
    public String getPatientId() {
        return decoratedAlert.getPatientId();
    }

    /**
     * Returns the condition of the wrapped alert.
     *
     * @return the condition string
     */
    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    /**
     * Returns the timestamp of the wrapped alert.
     *
     * @return the timestamp in milliseconds
     */
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }
}