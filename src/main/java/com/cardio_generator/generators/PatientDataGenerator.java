package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating data for a given patient.
 */
public interface PatientDataGenerator {
    /**
     * Triggers the generation of data for the given patient.
     *
     * @param patientId      the ID of the patient
     * @param outputStrategy the strategy used to output or store the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}