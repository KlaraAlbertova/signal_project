package com.alerts.strategies;

import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.Staff;

import java.util.List;

public class HeartRateStrategy implements AlertStrategy{
    private AlertFactory factory = new ECGAlertFactory();

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, AlertManager alertManager){
        if (records==null || records.isEmpty())
            return;

        records.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        int windowSize = 10;
        for (int i = windowSize; i < records.size(); i++) {
            double avg = 0;
            for (int j = i - windowSize; j < i; j++)
                avg += records.get(j).getMeasurementValue();

            avg /= windowSize;
            double current = records.get(i).getMeasurementValue();
            // Triggering an alert if current is 50% higher the average, which can be considered significant amount
            if (current > avg*1.5) {
                Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()), "ECG abnormal", records.get(i).getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }
}
