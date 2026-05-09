package com.alerts.alerts;

/**
 * Represents an alert generated for a blood oxygen saturation anomaly.
 */
public class BloodOxygenAlert extends Alert {

    /**
     * Constructs a new BloodOxygenAlert with the specified patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient this alert belongs to
     * @param condition a description of the health condition that triggered the alert
     * @param timestamp the timestamp of the patient record that triggered the alert
     */
    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
