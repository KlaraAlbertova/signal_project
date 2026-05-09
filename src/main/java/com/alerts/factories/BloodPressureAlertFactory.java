package com.alerts.factories;

import com.alerts.alerts.Alert;
import com.alerts.alerts.BloodPressureAlert;

/**
 * Factory for creating {@link BloodPressureAlert} objects.
 *
 * <p>Used when a blood pressure anomaly is detected.</p>
 */
public class BloodPressureAlertFactory extends AlertFactory {

    /**
     * Creates a {@link BloodPressureAlert} with the given parameters.
     *
     * @param patientId the ID of the patient the alert is for
     * @param condition a description of the blood pressure condition
     * @param timestamp the time the alert was triggered, in milliseconds since the Unix epoch
     * @return a new {@link BloodPressureAlert} instance
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
}
