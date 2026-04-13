package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating data for a given patient.
 */
public interface PatientDataGenerator {
    /**
     * Triggers the generation of data for the given patient.
     *
     * @param patientId int. The ID of the patient.
     * @param outputStrategy OutputStrategy. The OutputStrategy to be used to store or output the generate data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}