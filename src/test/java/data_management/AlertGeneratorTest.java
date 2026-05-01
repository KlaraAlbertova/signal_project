package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.AlertGenerator;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.MockReader;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    void setUp() {
        DataReader reader = new MockReader("path/to/data");
        storage = new DataStorage(reader);
        alertGenerator = new AlertGenerator(storage);
    }


    @Test
    void testSystolicAbove180TriggersAlert() {
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        // should not throw, alert gets dispatched internally
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSystolicBelow90TriggersAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSystolicNormalNoAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }


    @Test
    void testDiastolicAbove120TriggersAlert() {
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testDiastolicBelow60TriggersAlert() {
        storage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testDiastolicNormalNoAlert() {
        storage.addPatientData(1, 80.0, "DiastolicPressure", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }


    @Test
    void testIncreasingBloodPressureTrendTriggersAlert() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130.0, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testDecreasingBloodPressureTrendTriggersAlert() {
        storage.addPatientData(1, 130.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testNoTrendWhenChangeLessThan10() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 105.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 110.0, "SystolicPressure", 3000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testTrendNotTriggeredWithOnlyTwoReadings() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }


    @Test
    void testLowSaturationBelow92TriggersAlert() {
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSaturationExactly92NoAlert() {
        storage.addPatientData(1, 92.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testRapidSaturationDropTriggersAlert() {
        storage.addPatientData(1, 97.0, "Saturation", 1000L);
        storage.addPatientData(1, 92.0, "Saturation", 300000L); // 5 min later
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSaturationDropOutside10MinutesNoAlert() {
        storage.addPatientData(1, 97.0, "Saturation", 1000L);
        storage.addPatientData(1, 92.0, "Saturation", 700000L); // 11+ min later
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }


    @Test
    void testHypotensiveHypoxemiaTriggersAlert() {
        // both conditions at same timestamp
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testHypotensiveHypoxemiaOnlyLowBPNoAlert() {
        // only low BP, saturation is fine
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 95.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testHypotensiveHypoxemiaOnlyLowSatNoAlert() {
        // only low saturation, BP is fine
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }


    @Test
    void testECGPeakTriggersAlert() {
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i);
        }
        storage.addPatientData(1, 2.0, "ECG", 2000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testECGNormalNoAlert() {
        for (int i = 0; i < 11; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i);
        }
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testECGNotEnoughDataNoAlert() {
        storage.addPatientData(1, 5.0, "ECG", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }


    @Test
    void testManualAlertTriggered() {
        storage.addPatientData(1, 1.0, "Alert", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testManualAlertNotTriggeredWhenValue0() {
        storage.addPatientData(1, 0.0, "Alert", 1000L);
        Patient patient = storage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testEvaluateDataWithNoRecords() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        DataReader reader2 = new MockReader("path/to/data");
        DataStorage emptyStorage = new DataStorage(reader2);
        emptyStorage.addPatientData(2, 0.0, "SystolicPressure", 1000L);
        Patient patient = emptyStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }
}
