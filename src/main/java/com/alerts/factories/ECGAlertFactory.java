package com.alerts.factories;

import com.alerts.alerts.Alert;
import com.alerts.alerts.ECGAlert;

/**
 * Factory for creating {@link ECGAlert} objects.
 *
 * <p>Used when an irregular heart rate or ECG abnormality is detected.</p>
 */
public class ECGAlertFactory extends AlertFactory {

    /**
     * Creates an {@link ECGAlert} with the given parameters.
     *
     * @param patientId the ID of the patient the alert is for
     * @param condition a description of the ECG condition
     * @param timestamp the time the alert was triggered, in milliseconds since the Unix epoch
     * @return a new {@link ECGAlert} instance
     */
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
}
