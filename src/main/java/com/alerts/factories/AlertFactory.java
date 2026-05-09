package com.alerts.factories;

import com.alerts.alerts.Alert;

/**
 * Base factory class for creating {@link Alert} objects.
 *
 * <p>Subclasses override {@link #createAlert} to return a specific
 * alert type without the caller needing to know the concrete class.</p>
 */
public class AlertFactory {

    /**
     * Creates a generic {@link Alert} with the given parameters.
     *
     * @param patientId the ID of the patient the alert is for
     * @param condition a description of the condition that triggered the alert
     * @param timestamp the time the alert was triggered, in milliseconds since the Unix epoch
     * @return a new {@link Alert} instance
     */
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, condition, timestamp);
    }
}