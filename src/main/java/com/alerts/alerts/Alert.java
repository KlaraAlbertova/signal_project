package com.alerts.alerts;

/**
 * Represents an alert generated for a patient when a health condition
 * exceeds a predefined threshold.
 */
public class Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Constructs a new Alert with the specified patient ID, condition, and timestamp.
     *
     * @param patientId the unique identifier of the patient this alert belongs to
     * @param condition a description of the health condition that triggered the alert
     * @param timestamp the timestamp of the patient record that triggered the alert
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    /**
     * Returns the patient ID associated with this alert.
     *
     * @return the patient ID
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Returns the condition that triggered this alert.
     *
     * @return the condition string
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Returns the timestamp of the patient record that triggered this alert.
     *
     * @return the timestamp of the triggering patient record
     */
    public long getTimestamp() {
        return timestamp;
    }
}