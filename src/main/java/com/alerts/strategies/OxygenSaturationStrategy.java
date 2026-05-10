package com.alerts.strategies;

import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.patients.Patient;
import com.data_management.patients.PatientRecord;
import com.data_management.Staff;

import java.util.List;

/**
 * Strategy for monitoring blood oxygen saturation levels.
 *
 * <p>Triggers an alert if saturation drops below 92%, or if there is
 * a rapid drop of 5+ % within a 10-minute window.</p>
 */
public class OxygenSaturationStrategy implements AlertStrategy {
    private AlertFactory factory = new BloodOxygenAlertFactory();

    /**
     * Evaluates blood oxygen saturation records for the given patient and
     * dispatches alerts for critical levels or rapid drops.
     *
     * @param patient      the patient being evaluated
     * @param records      the list of blood oxygen saturation records to check
     * @param alertManager the alert manager used to dispatch alerts
     */
    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, AlertManager alertManager) {
        if (records == null || records.isEmpty())
            return;

        records.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getMeasurementValue() < 92) {
                Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()),
                        "Blood oxygen saturation bellow 92%", records.get(i).getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }

            for (int j = i - 1; j >= 0; j--) {
                long timeDiff = records.get(i).getTimestamp() - records.get(j).getTimestamp();
                if (timeDiff > 600_000) break;
                if (records.get(j).getMeasurementValue() - records.get(i).getMeasurementValue() >= 5) {
                    Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()),
                            "Rapid drop of blood oxygen saturation", records.get(i).getTimestamp());
                    alertManager.dispatchAlert(alert, List.of(new Staff(0)));
                    break;
                }
            }
        }
    }
}
