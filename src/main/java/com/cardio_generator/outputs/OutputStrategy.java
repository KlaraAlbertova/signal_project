package com.cardio_generator.outputs;

/**
 * Interface for outputting patient data.
 */
public interface OutputStrategy {
    /**
     * Outputs the patient data.
     * <p>
     * Implementation-specific behavior dictates whether this data is printed,
     * saved to a file, or transmitted over a network connection.
     * </p>
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the type of data being recorded
     * @param data      the data value to be output
     */
    void output(int patientId, long timestamp, String label, String data);
}