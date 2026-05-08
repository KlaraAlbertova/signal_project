package com.alerts.strategies;

import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.Staff;

import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    private AlertFactory factory = new BloodPressureAlertFactory();

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

    public void checkSystolicBloodPressure(Patient patient, List<PatientRecord> records, AlertManager alertManager){
        if (records==null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue() > 180 ) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure exceeds 180 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
            else if(patientRecord.getMeasurementValue() < 90) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure is bellow 90 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }
    public void checkDiastolicBloodPressure(Patient patient, List<PatientRecord> records, AlertManager alertManager){
        if (records==null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue() > 120 ) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Diastolic pressure exceeds 120 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
            else if(patientRecord.getMeasurementValue() < 60) {
                Alert alert = factory.createAlert(String.valueOf(patientRecord.getPatientId()), "Diastolic  pressure is bellow 60 mmHg", patientRecord.getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }

    public void checkBloodPressureTrends(Patient patient, List<PatientRecord> records, AlertManager alertManager){
        if (records.size()<3)
            return;

        for (int i = 0; i < records.size() - 2; i++) {
            double first = records.get(i).getMeasurementValue();
            double second = records.get(i+1).getMeasurementValue();
            double third = records.get(i+2).getMeasurementValue();

            if(second-first > 10 && third-second > 10) {
                Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()), "Increasing blood pressure", records.get(i).getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
            else if(first - second > 10 && second - third > 10) {
                Alert alert = factory.createAlert(String.valueOf(records.get(i).getPatientId()), "Decreasing blood pressure", records.get(i).getTimestamp());
                alertManager.dispatchAlert(alert, List.of(new Staff(0)));
            }
        }
    }
}
