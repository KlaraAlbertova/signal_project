package com.data_management;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a patient and manages their medical records.
 *
 * <p>Stores patient-specific data and provides methods for adding records
 * and retrieving them in full or filtered by a time range.</p>
 */
public class Patient {
    private int patientId;
    private List<PatientRecord> patientRecords;

    /**
     * Constructs a new {@code Patient} with the specified ID and an empty record list.
     *
     * @param patientId the unique identifier for this patient
     */
    public Patient(int patientId) {
        this.patientId = patientId;
        this.patientRecords = new ArrayList<>();
    }

    /**
     * Adds a new record to this patient's list of medical records.
     *
     * @param measurementValue the measurement value to store in the record
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since UNIX epoch
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp);
        this.patientRecords.add(record);
    }

    /**
     * Retrieves a list of PatientRecord objects for this patient that fall within a
     * specified time range.
     * The method filters records based on the start and end times provided.
     *
     * @param startTime the start of the time range, in milliseconds since UNIX
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since UNIX epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        ArrayList<PatientRecord> subRecords = new ArrayList<>();
        for (PatientRecord record : patientRecords) {
            if(record.getTimestamp() >= startTime && record.getTimestamp() <= endTime) {
                subRecords.add(record);
            }
        }
        return subRecords;
    }

    /**
     * Returns the complete list of all records associated with this patient,
     * regardless of timestamp.
     *
     * @return a list of all {@link PatientRecord} objects for this patient
     */
    public List<PatientRecord> getAllRecords() {
        return patientRecords;
    }
}
