package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertManager;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.MockReader;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;
    private AlertManager alertManager;

    @BeforeEach
    void setUp() {
        DataReader reader = new MockReader("path/to/data");
        storage = new DataStorage(reader);
        alertGenerator = new AlertGenerator(storage);
        alertManager = alertGenerator.getAlertManager();
    }

    // SystolicPressure
    @Test
    void testSystolicAbove180TriggersAlert() {
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Systolic pressure exceeds 180 mmHg", alerts.get(0).getCondition());
    }

    @Test
    void testSystolicBelow90TriggersAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Systolic pressure is bellow 90 mmHg", alerts.get(0).getCondition());
    }

    @Test
    void testSystolicNormalNoAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testIncreasingBloodPressureTrendTriggersAlert() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130.0, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Increasing blood pressure", alerts.get(0).getCondition());
    }

    @Test
    void testDecreasingBloodPressureTrendTriggersAlert() {
        storage.addPatientData(1, 130.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Decreasing blood pressure", alerts.get(0).getCondition());
    }

    @Test
    void testNoTrendWhenChangeLessThan10() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 105.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 110.0, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testTrendNotTriggeredWithOnlyTwoReadings() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    // DiastolicPressure
    @Test
    void testDiastolicAbove120TriggersAlert() {
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Diastolic pressure exceeds 120 mmHg", alerts.get(0).getCondition());
    }

    @Test
    void testDiastolicBelow60TriggersAlert() {
        storage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Diastolic  pressure is bellow 60 mmHg", alerts.get(0).getCondition());
    }

    @Test
    void testDiastolicNormalNoAlert() {
        storage.addPatientData(1, 80.0, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    // Saturation
    @Test
    void testLowSaturationBelow92TriggersAlert() {
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Blood saturation bellow 92%", alerts.get(0).getCondition());
    }

    @Test
    void testSaturationExactly92NoAlert() {
        storage.addPatientData(1, 92.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testRapidSaturationDropTriggersAlert() {
        storage.addPatientData(1, 97.0, "Saturation", 1000L);
        storage.addPatientData(1, 92.0, "Saturation", 300000L); // 5 min later
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertTrue(alerts.stream().anyMatch(a -> a.getCondition().equals("Rapid drop of blood saturation")));
    }

    @Test
    void testSaturationDropOutside10MinutesNoAlert() {
        storage.addPatientData(1, 97.0, "Saturation", 1000L);
        storage.addPatientData(1, 92.0, "Saturation", 700000L); // 11+ min later
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertFalse(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Rapid drop of blood saturation")));
    }

    // HypotensiveHypoxemia

    @Test
    void testHypotensiveHypoxemiaTriggersAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertTrue(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Hypotensive Hypoxemia Alert")));
    }

    @Test
    void testHypotensiveHypoxemiaOnlyLowBPNoAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 95.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertFalse(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Hypotensive Hypoxemia Alert")));
    }

    @Test
    void testHypotensiveHypoxemiaOnlyLowSatNoAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertFalse(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Hypotensive Hypoxemia Alert")));
    }

    // ECG
    @Test
    void testECGPeakTriggersAlert() {
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i);
        }
        storage.addPatientData(1, 2.0, "ECG", 2000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertTrue(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("ECG abnormal data")));
    }

    @Test
    void testECGNormalNoAlert() {
        for (int i = 0; i < 11; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i);
        }
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testECGNotEnoughDataNoAlert() {
        storage.addPatientData(1, 5.0, "ECG", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    // TriggeredAlerts
    @Test
    void testManualAlertTriggered() {
        storage.addPatientData(1, 1.0, "Alert", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Manual alert triggered", alerts.get(0).getCondition());
    }

    @Test
    void testManualAlertNotTriggeredWhenValue0() {
        storage.addPatientData(1, 0.0, "Alert", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    // NoRecords
    @Test
    void testEvaluateDataWithNoRecords() {
        DataReader reader2 = new MockReader("path/to/data");
        DataStorage emptyStorage = new DataStorage(reader2);
        emptyStorage.addPatientData(2, 120.0, "SystolicPressure", 1000L);
        Patient patient = emptyStorage.getAllPatients().get(0);
        alertGenerator.evaluateData(patient);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }
}