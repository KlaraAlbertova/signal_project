package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Implementation of {@link PatientDataGenerator} that simulates patient alert events.
 * Tracks the alert state (triggered or resolved) for a given number of patients.
 */
public class AlertGenerator implements PatientDataGenerator {

    // Changed name variable from randomGenerator
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name form AlertStates
    private boolean[] alertStates; // false = resolved, true = triggered

    /**
     * Constructs an {@code AlertGenerator} for the specified number of patients.
     *
     * @param patientCount the number of patients to generate alert data for
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }


    /**
     * Triggers or resolves an alert for the given patient and outputs the result.
     *
     * <p>If the patient currently has an active alert, there is a 90% chance it will
     * be resolved. If no alert is active, a new one may be triggered based on a
     * Poisson-derived probability using rate λ (lambda = 0.1).</p>
     *
     * @param patientId      the ID of the patient
     * @param outputStrategy the strategy used to output or store the generated data
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