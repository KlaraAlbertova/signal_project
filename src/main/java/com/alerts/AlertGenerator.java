package com.alerts;

import com.alerts.alerts.Alert;
import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.patients.Patient;
import com.data_management.patients.PatientRecord;
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

    /** Strategy that checks systolic and diastolic blood pressure thresholds and trends. */
    private final AlertStrategy bloodPressureStrategy = new BloodPressureStrategy();

    /** Strategy that detects abnormal ECG / heart-rate spikes. */
    private final AlertStrategy heartRateStrategy = new HeartRateStrategy();

    /** Strategy that checks blood oxygen saturation levels and rapid drops. */
    private final AlertStrategy oxygenSaturationStrategy = new OxygenSaturationStrategy();

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
        List<PatientRecord> bloodPressureRecords = new ArrayList<>();
        List<PatientRecord> bloodSaturation = new ArrayList<>();
        List<PatientRecord> ECG = new ArrayList<>();
        List<PatientRecord> triggeredAlert = new ArrayList<>();


        for(PatientRecord patientRecord : records) {
            if (patientRecord.getRecordType().equals("SystolicPressure")) {
                systolicPressure.add(patientRecord);
                bloodPressureRecords.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("DiastolicPressure")) {
                bloodPressureRecords.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("Saturation")) {
                bloodSaturation.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("ECG")) {
                ECG.add(patientRecord);
            }else if (patientRecord.getRecordType().equals("Alert")) {
                triggeredAlert.add(patientRecord);
            }
        }


        bloodPressureStrategy.checkAlert(patient, bloodPressureRecords, alertManager);
        oxygenSaturationStrategy.checkAlert(patient, bloodSaturation, alertManager);
        heartRateStrategy.checkAlert(patient, ECG, alertManager);

        checkHypotensiveHypoxemia(patient, systolicPressure, bloodSaturation);
        checkTriggeredAlerts(patient, triggeredAlert);
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
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        List<PatientRecord> sorted = new ArrayList<>(records);
        sorted.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
        return sorted;
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
