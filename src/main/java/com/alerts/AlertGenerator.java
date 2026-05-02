package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.Staff;

import java.util.ArrayList;
import java.util.List;

//Change: Javadoc cleanup
/**
 * Monitors patient data and generates alerts when predefined health conditions are met.
 *
 * <p>This class relies on a {@link DataStorage} instance to access patient data and evaluate
 *  it against specific health criteria.</p>
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertManager alertManager;
    // Change: seperated the code into multiple lines for better readability
    //Change: Javadoc cleanup
    /**
     * Constructs an AlertGenerator with a specific data source.
     *
     * @param dataStorage The data storage system that provides access to patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertManager = new AlertManager();
    }

    //Change: Javadoc cleanup
    /**
     * Evaluates all available records for the given patient and triggers alerts
     * for any conditions that exceed defined thresholds.
     *
     * <p>The following checks are performed:</p>
     * <ul>
     *   <li>Systolic blood pressure critical values and trends</li>
     *   <li>Diastolic blood pressure critical values and trends</li>
     *   <li>Blood oxygen saturation levels and rapid drops</li>
     *   <li>Hypotensive hypoxemia (combined low systolic and low saturation)</li>
     *   <li>ECG abnormalities</li>
     *   <li>Manually triggered alerts</li>
     * </ul>
     *
     * @param patient the patient whose records are to be evaluated
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

    // TODO not mock staf but staf that is assigned to the patient in all of the checks

    /**
     * Checks systolic blood pressure readings and triggers an alert if any value
     * exceeds 180 mmHg or falls below 90 mmHg.
     *
     * @param patient the patient being evaluated
     * @param records the list of systolic pressure records to check
     */
    private void checkSystolicBloodPressure(Patient patient,List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue() > 180 ) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure exceeds 180 mmHg" , patientRecord.getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
            else if(patientRecord.getMeasurementValue() < 90) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Systolic pressure is bellow 90 mmHg", patientRecord.getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
        }
    }

    /**
     * Checks diastolic blood pressure readings and triggers an alert if any value
     * exceeds 120 mmHg or falls below 60 mmHg.
     *
     * @param patient the patient being evaluated
     * @param records the list of diastolic pressure records to check
     */
    private void checkDiastolicBloodPressure(Patient patient,List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return;

        for (PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue() > 120 ) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Diastolic pressure exceeds 120 mmHg" , patientRecord.getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
            else if(patientRecord.getMeasurementValue() < 60) {
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()), "Diastolic  pressure is bellow 60 mmHg" , patientRecord.getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
        }
    }

    /**
     * Checks blood pressure trends, an alert is triggered if three consecutive readings each
     * differ by more than 10 mmHg in the same direction.
     *
     * @param patient the patient being evaluated
     * @param records the list of blood pressure records (systolic or diastolic) to check
     */
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
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
            else if(first - second > 10 && second - third > 10) {
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "Decreasing blood pressure" , records.get(i).getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
        }
    }

    /**
     * Checks diastolic blood saturation readings and triggers an alert if any value
     * is below 92% or there is drop of 5% or more within a 10-minute window.
     *
     * @param patient the patient being evaluated
     * @param records the list of blood saturation records to check
     */
    private void checkBloodSaturation(Patient patient,List<PatientRecord> records){
        if (records==null || records.isEmpty())
            return;

        records=sortPatientRecord(records);

        for (int i =0; i<records.size(); i++) {
            if(records.get(i).getMeasurementValue() < 92 ) {
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "Blood saturation bellow 92%" , records.get(i).getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }

            for (int j = i - 1; j >= 0; j--) {
                long timeDiff = records.get(i).getTimestamp() - records.get(j).getTimestamp();
                if (timeDiff > 600_000) break;
                if (records.get(j).getMeasurementValue() - records.get(i).getMeasurementValue() >= 5) {
                    Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                            "Rapid drop of blood saturation" , records.get(i).getTimestamp());
                    Staff mockstaff = new Staff(0);
                    triggerAlert(alert, List.of(mockstaff));
                    break;
                }
            }
        }
    }

    /**
     * Checks for hypotensive hypoxemia by identifying timestamps where systolic pressure
     * is below 90 mmHg and blood oxygen saturation is below 92% simultaneously.
     *
     * @param patient          the patient being evaluated
     * @param systolicPressure the list of systolic pressure records to check
     * @param bloodSaturation  the list of blood saturation records to check
     */
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
                    Staff mockstaff = new Staff(0);
                    triggerAlert(alert, List.of(mockstaff));
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

    /**
     * Analyzes ECG readings using a sliding window of 10 samples. An alert is triggered
     * if the current reading exceeds 150% of the rolling average, indicating an abnormal spike.
     *
     * @param patient the patient being evaluated
     * @param records the list of ECG records to check
     */
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
            // Triggering an alert if current is 50% higher the average, which can be considered significant amount
            if (current > avg*1.5) {
                Alert alert = new Alert(String.valueOf(records.get(i).getPatientId()),
                        "ECG abnormal data" , records.get(i).getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
        }
    }

    /**
     * Checks for manually triggered alerts in the patient's records. An alert is
     * dispatched if a record of type {@code "Alert"} has a measurement value of {@code 1}.
     *
     * @param patient the patient being evaluated
     * @param records the list of alert-type records to check
     */
    private void checkTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        if (records==null || records.isEmpty())
            return;

        // assuming that this alert will be in the PatientRecords
        // assuming measurementValues
        for(PatientRecord patientRecord : records) {
            if(patientRecord.getMeasurementValue()==1) { // 1 == the alert is triggered, 0 == the alert is not triggered
                Alert alert = new Alert(String.valueOf(patientRecord.getPatientId()),
                        "Manual alert triggered", patientRecord.getTimestamp());
                Staff mockstaff = new Staff(0);
                triggerAlert(alert, List.of(mockstaff));
            }
        }
    }

    /**
     * Sorts a list of {@link PatientRecord} objects in ascending order by timestamp
     * using bubble sort.
     *
     * @param records the list of records to sort
     * @return the sorted list, or {@code null} if the input is null or empty
     */
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

    //Change: Javadoc cleanup
    /**
     * Dispatches the given alert to the specified list of staff members via the
     * {@link AlertManager}.
     *
     * @param alert the alert to be dispatched, containing the condition details and timestamp
     * @param staff list of the staff members to be notified.
     */
    private void triggerAlert(Alert alert, List<Staff> staff) {
        alertManager.dispatchAlert(alert, staff);
    }

    public AlertManager getAlertManager() {return alertManager;}
}
