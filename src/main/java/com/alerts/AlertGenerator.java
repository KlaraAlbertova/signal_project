package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.Staff;

import java.util.ArrayList;
import java.util.List;

//Change: Javadoc cleanup
/**
 * Monitors patient data and generates alerts when certain predefined conditions are met.
 * This class relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertManager alertManager;
    // Change: seperated the code into multiple lines for better readability
    //Change: Javadoc cleanup
    /**
     * Constructs an AlertGenerator with a specific data source.
     *
     * @param dataStorage DataStorage. The data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertManager = new AlertManager();
    }

    //Change: Javadoc cleanup
    /**
     * Evaluates the specified patient's data to determine if any alert conditions are met.
     * If a condition is met, an alert is triggered via the {@link #triggerAlert} method.
     *
     * @param patient Patient.The patient data to evaluate for alert conditions.
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getAllRecords();

        List<PatientRecord> systolicPressure = new ArrayList<>();
        List<PatientRecord> diastolicPressure = new ArrayList<>();
        List<PatientRecord> bloodSaturation = new ArrayList<>();
        List<PatientRecord> ECG = new ArrayList<>();
        List<PatientRecord> triggeredAlert = new ArrayList<>();


        for(PatientRecord patientRecord : records) {
            if (patientRecord.getRecordType().equals("SystolicPressure")) {
                systolicPressure.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("DiastolicPressure")) {
                diastolicPressure.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("Saturation")) {
                bloodSaturation.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("ECG")) {
                ECG.add(patientRecord);
            }else if (patientRecord.getRecordType().equals("Alert")) {
                triggeredAlert.add(patientRecord);
            }
        }

        checkSystolicBloodPressure(patient,systolicPressure);
        checkBloodPressureTrends(patient, systolicPressure);

        checkDiastolicBloodPressure(patient,diastolicPressure);
        checkBloodPressureTrends(patient, diastolicPressure);

        checkBloodSaturation(patient, bloodSaturation);

        checkHypotensiveHypoxemia(patient, systolicPressure, bloodSaturation);

        checkECG(patient, ECG);

        checkTriggeredAlerts(patient, triggeredAlert);
    }

    // TODO not null staf but staf that is assigned to the patient

    private void checkSystolicBloodPressure(Patient patient,List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue() > 180 ) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure exceeds 180 mmHg" , patientRecord.getTimestamp());
                triggerAlert(alert, null);
            }
            else if(patientRecord.getMeasurementValue() < 90) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure is bellow 90 mmHg", patientRecord.getTimestamp());
                triggerAlert(alert, null);
            }
        }
    }

    private void checkDiastolicBloodPressure(Patient patient,List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue() > 120 ) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Diastolic pressure exceeds 120 mmHg" , patientRecord.getTimestamp());
                triggerAlert(alert, null);
            }
            else if(patientRecord.getMeasurementValue() < 60) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Diastolic  pressure is bellow 60 mmHg" , patientRecord.getTimestamp());
                triggerAlert(alert, null);
            }
        }
    }

    private List<PatientRecord> sortPatientRecord(List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return null;

        for (int i = 0; i < records.size() - 1; i++) {
            for (int j = 0; j < records.size() - 1 - i; j++) {
                if (records.get(j).getTimestamp() > records.get(j + 1).getTimestamp()) {
                    PatientRecord temp = records.get(j);
                    records.set(j, records.get(j + 1));
                    records.set(j + 1, temp);
                }
            }
        }

        return records;
    }

    private void checkBloodPressureTrends(Patient patient,List<PatientRecord> records){
        if (records.size()<3)
            return;

        records= sortPatientRecord(records);

        for (int i = 0; i < records.size() - 2; i++) {
            double first = records.get(i).getMeasurementValue();
            double second = records.get(i+1).getMeasurementValue();
            double third = records.get(i+2).getMeasurementValue();

            if(second-first > 10 && third-second > 10) {
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "Increasing blood pressure" , records.get(i).getTimestamp());
                triggerAlert(alert, null);
            }
            else if(first - second > 10 && second - third > 10) {
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "Decreasing blood pressure" , records.get(i).getTimestamp());
                triggerAlert(alert, null);
            }
        }
    }

    private void checkBloodSaturation(Patient patient,List<PatientRecord> records){
        if (records==null || records.isEmpty())
            return;

        records=sortPatientRecord(records);

        for (int i =0; i<records.size(); i++) {
            if(records.get(i).getMeasurementValue() < 92 ) {
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "Blood saturation bellow 92%" , records.get(i).getTimestamp());
                triggerAlert(alert, null);
            }

            for (int j = i - 1; j >= 0; j--) {
                long timeDiff = records.get(i).getTimestamp() - records.get(j).getTimestamp();
                if (timeDiff > 600_000) break;
                if (records.get(j).getMeasurementValue() - records.get(i).getMeasurementValue() >= 5) {
                    Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                            "Rapid drop of blood saturation" , records.get(i).getTimestamp());
                    triggerAlert(alert, null);
                    break;
                }
            }
        }
    }

    private void checkHypotensiveHypoxemia (Patient patient,List<PatientRecord> systolicPressure, List<PatientRecord> bloodSaturation){
        if (systolicPressure==null || systolicPressure.isEmpty() || bloodSaturation==null || bloodSaturation.isEmpty())
            return;

        systolicPressure = sortPatientRecord(systolicPressure);
        bloodSaturation = sortPatientRecord(bloodSaturation);

        int n =  systolicPressure.size() + bloodSaturation.size();
        int sp=0;
        int bs=0;
        for(int i =0; i<n;i++) {
            long spTimeStamp = systolicPressure.get(sp).getTimestamp();
            long bsTimeStamp = bloodSaturation.get(bs).getTimestamp();

            if(spTimeStamp == bsTimeStamp) {
                if(systolicPressure.get(sp).getMeasurementValue() < 90 && bloodSaturation.get(bs).getMeasurementValue() < 92) {
                    Alert alert = new Alert(String.valueOf(systolicPressure.get(sp).getPatientId()), "Hypotensive Hypoxemia Alert", spTimeStamp);
                    triggerAlert(alert, null);
                }
                sp++;
                bs++;
            }
            else if(spTimeStamp > bsTimeStamp) {
                bs++;
            }
            else{
                sp++;
            }

            if(sp>= systolicPressure.size() || bs>= bloodSaturation.size()) {
                break;
            }
        }

    }

    private void checkECG(Patient patient,List<PatientRecord> records){
        if (records==null || records.isEmpty())
            return;

        records= sortPatientRecord(records);

        int windowSize = 10;
        for (int i = windowSize; i < records.size(); i++) {
            double avg = 0;
            for (int j = i - windowSize; j < i; j++)
                avg += records.get(j).getMeasurementValue();

            avg /= windowSize;
            double current = records.get(i).getMeasurementValue();
            if (current > avg*1.5) { // Triggering an alert if current is 50% higher the average
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "ECG abnormal data" , records.get(i).getTimestamp());
                triggerAlert(alert, null);
            }
        }


    }

    private void checkTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return;

        for(PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue()==1) { // 1 == the alert is triggered, 0 == the alert is not triggered
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()),
                        "Manual alert triggered", patientRecord.getTimestamp());
                triggerAlert(alert, null);
            }
        }
    }

    //Change: Javadoc cleanup
    /**
     * Dispatches the alert to the specified staff member.

     * @param alert Alert.The alert object containing details about the alert condition. The alert should be fully formed.
     * @param staff List of the staff members to be notified.
     */
    private void triggerAlert(Alert alert, List<Staff> staff) {
        alertManager.dispatchAlert(alert, staff);
    }
}
