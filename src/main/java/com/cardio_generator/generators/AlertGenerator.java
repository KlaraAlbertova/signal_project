package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Implementation of the {@link PatientDataGenerator}.
 * Tracks the alert state (triggered or resolved) for give number of patients.
 */

public class AlertGenerator implements PatientDataGenerator {

    // Changed name variable from randomGenerator
    // Changed from public
    private static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name form AlertStates
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an AlertGenerator for a specific number of patients.
     *
     * @param patientCount int. The number of patients to generate data for.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Triggers or resolves an alert for given patient.
     * * <p>If a patient currently has an active alert, there is a 90% chance it will resolve.
     * If no alert is active, a new one is triggered based on a probability derived from
     * the rate λ (lambda).</p>
     *
     * @param patientId int. The ID of the patient.
     * @param outputStrategy OutputStrategy. The OutputStrategy to be used to store or output the generate data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name form Lambda
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}