package alerts.strategies;

import com.alerts.AlertManager;
import com.alerts.strategies.BloodPressureStrategy;
import com.data_management.patients.Patient;
import com.data_management.patients.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BloodPressureStrategyTest {

    private AlertManager alertManager;
    private Patient patient;
    private BloodPressureStrategy strategy;

    @BeforeEach
    void setUp() {
        alertManager = new AlertManager();
        patient = new Patient(1);
        strategy = new BloodPressureStrategy();
    }

    @Test
    void testHighSystolicTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 185.0, "SystolicPressure", 1000L));

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertTrue(alertManager.getDispatchedAlerts().get(0).getCondition().contains("180"));
    }

    @Test
    void testLowSystolicTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85.0, "SystolicPressure", 1000L));

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertTrue(alertManager.getDispatchedAlerts().get(0).getCondition().contains("90"));
    }

    @Test
    void testHighDiastolicTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 125.0, "DiastolicPressure", 1000L));

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertTrue(alertManager.getDispatchedAlerts().get(0).getCondition().contains("120"));
    }

    @Test
    void testLowDiastolicTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 55.0, "DiastolicPressure", 1000L));

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertTrue(alertManager.getDispatchedAlerts().get(0).getCondition().contains("60"));
    }

    @Test
    void testIncreasingTrendTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 100.0, "SystolicPressure", 1000L));
        records.add(new PatientRecord(1, 115.0, "SystolicPressure", 2000L));
        records.add(new PatientRecord(1, 130.0, "SystolicPressure", 3000L));

        strategy.checkAlert(patient, records, alertManager);

        assertTrue(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Increasing")));
    }

    @Test
    void testDecreasingTrendTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 130.0, "SystolicPressure", 1000L));
        records.add(new PatientRecord(1, 115.0, "SystolicPressure", 2000L));
        records.add(new PatientRecord(1, 100.0, "SystolicPressure", 3000L));

        strategy.checkAlert(patient, records, alertManager);

        assertTrue(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Decreasing")));
    }

    @Test
    void testNormalReadingNoAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 120.0, "SystolicPressure", 1000L));

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testNullRecordsNoAlert() {
        strategy.checkAlert(patient, null, alertManager);
        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testEmptyRecordsNoAlert() {
        strategy.checkAlert(patient, new ArrayList<>(), alertManager);
        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }
}
