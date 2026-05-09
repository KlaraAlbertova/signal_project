package com.alerts.factories;

import com.alerts.alerts.Alert;
import com.alerts.alerts.BloodOxygenAlert;

/**
 * Factory for creating {@link BloodOxygenAlert} objects.
 *
 * <p>Used when a significant change in blood oxygen levels is detected.</p>
 */
public class BloodOxygenAlertFactory extends AlertFactory {

    /**
     * Creates a {@link BloodOxygenAlert} with the given parameters.
     *
     * @param patientId the ID of the patient the alert is for
     * @param condition a description of the blood oxygen condition
     * @param timestamp the time the alert was triggered, in milliseconds since the Unix epoch
     * @return a new {@link BloodOxygenAlert} instance
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}

