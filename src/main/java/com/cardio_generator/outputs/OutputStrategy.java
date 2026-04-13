package com.cardio_generator.outputs;

/**
 * Interface for outputting of the patient's data.
 */
public interface OutputStrategy {
    /**
     * Outputs the patient data.
     * <p>
     * Implementation-specific behavior dictates whether this data is printed,
     * saved to a database
     * </p>
     *
     * @param patientId int. The ID of the patient.
     * @param timestamp long. The time the data was recorded.
     * @param label String. The type of data being recorded.
     * @param data String. The data to be stored.
     */
    void output(int patientId, long timestamp, String label, String data);
}