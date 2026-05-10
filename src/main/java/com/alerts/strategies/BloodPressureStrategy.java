package com.alerts.strategies;

import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.patients.Patient;
import com.data_management.patients.PatientRecord;
import com.data_management.Staff;

import java.util.List;

/**
 * Strategy for monitoring blood pressure readings.
 *
 * <p>Checks both systolic and diastolic pressure against critical thresholds,
 * and detects increasing or decreasing trends across consecutive readings.</p>
 */
public class BloodPressureStrategy implements AlertStrategy {
    private AlertFactory factory = new BloodPressureAlertFactory();

    /**
     * Evaluates blood pressure records for the given patient and dispatches
     * alerts for critical values or trends.
     *
     * @param patient      the patient being evaluated
     * @param records      the list of blood pressure records to check
     * @param alertManager the alert manager used to dispatch alerts
     */
    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, AlertManager alertManager) {
        if (records == null || records.isEmpty()) return;

        List<PatientRecord> systolic = new java.util.ArrayList<>();
        List<PatientRecord> diastolic = new java.util.ArrayList<>();

        for (PatientRecord r : records) {
            if (r.getRecordType().equals("SystolicPressure")) systolic.add(r);
            else if (r.getRecordType().equals("DiastolicPressure")) diastolic.add(r);
        }

        systolic.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
        diastolic.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        checkSystolicBloodPressure(patient, systolic, alertManager);
        checkDiastolicBloodPressure(patient, diastolic, alertManager);
        checkBloodPressureTrends(patient, systolic, alertManager);
        checkBloodPressureTrends(patient, diastolic, alertManager);
    }

    /**
     * Checks systolic blood pressure readings and triggers an alert if any value
     * exceeds 180 mmHg or falls below 90 mmHg.
     *
     * @param patient      the patient being evaluated
     * @param records      the list of systolic pressure records to check
     * @param alertManager the alert manager used to dispatch alerts
     */
    public void checkSystolicBloodPressure(Patient patient, List<PatientRecord> records, AlertManager alertManager) {
        if (records == null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if (patientRecord.getMeasurementValue() > 180) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure exceeds 180 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            } else if (patientRecord.getMeasurementValue() < 90) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure is bellow 90 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }

    /**
     * Checks diastolic blood pressure readings and triggers an alert if any value
     * exceeds 120 mmHg or falls below 60 mmHg.
     *
     * @param patient      the patient being evaluated
     * @param records      the list of diastolic pressure records to check
     * @param alertManager the alert manager used to dispatch alerts
     */
    public void checkDiastolicBloodPressure(Patient patient, List<PatientRecord> records, AlertManager alertManager) {
        if (records == null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if (patientRecord.getMeasurementValue() > 120) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Diastolic pressure exceeds 120 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            } else if (patientRecord.getMeasurementValue() < 60) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Diastolic  pressure is bellow 60 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }

    /**
     * Checks blood pressure trends and triggers an alert if three consecutive readings
     * each differ by more than 10 mmHg in the same direction.
     *
     * @param patient      the patient being evaluated
     * @param records      the list of blood pressure records to check
     * @param alertManager the alert manager used to dispatch alerts
     */
    public void checkBloodPressureTrends(Patient patient, List<PatientRecord> records, AlertManager alertManager) {
        if (records.size() < 3)
            return;

        for (int i = 0; i < records.size() - 2; i++) {
            double first = records.get(i).getMeasurementValue();
            double second = records.get(i + 1).getMeasurementValue();
            double third = records.get(i + 2).getMeasurementValue();

            if (second - first > 10 && third - second > 10) {
                Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()), "Increasing blood pressure", records.get(i).getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            } else if (first - second > 10 && second - third > 10) {
                Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()), "Decreasing blood pressure", records.get(i).getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }
}