package com.alerts.strategies;

import com.alerts.AlertManager;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * Strategy interface for evaluating patient health data and triggering alerts.
 *
 * <p>Each implementation encapsulates a specific monitoring algorithm
 * for a particular health metric.</p>
 */
public interface AlertStrategy {
    /**
     * Evaluates the given patient records and dispatches alerts if conditions are met.
     *
     * @param patient       the patient being evaluated
     * @param records       the list of patient records to check
     * @param alertManager  the alert manager used to dispatch alerts
     */
    void checkAlert(Patient patient, List<PatientRecord> records, AlertManager alertManager);
}
