package alerts.strategies;


import com.alerts.AlertManager;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OxygenSaturationStrategyTest {

    private AlertManager alertManager;
    private Patient patient;
    private OxygenSaturationStrategy strategy;

    @BeforeEach
    void setUp() {
        alertManager = new AlertManager();
        patient = new Patient(1);
        strategy = new OxygenSaturationStrategy();
    }

    @Test
    void testLowSaturationTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 88.0, "Saturation", 1000L));

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertTrue(alertManager.getDispatchedAlerts().get(0).getCondition().contains("92%"));
    }

    @Test
    void testRapidDropTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 97.0, "Saturation", 1000L));
        records.add(new PatientRecord(1, 91.0, "Saturation", 60000L));

        strategy.checkAlert(patient, records, alertManager);

        assertTrue(alertManager.getDispatchedAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Rapid drop")));
    }

    @Test
    void testDropOutsideWindowNoAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 97.0, "Saturation", 1000L));
        records.add(new PatientRecord(1, 91.0, "Saturation", 700000L)); // more than 10 minutes

        strategy.checkAlert(patient, records, alertManager);

        // only the low saturation alert, not the rapid drop
        assertTrue(alertManager.getDispatchedAlerts().stream()
                .noneMatch(a -> a.getCondition().contains("Rapid drop")));
    }

    @Test
    void testNormalSaturationNoAlert() {
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 95.0, "Saturation", 1000L));

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
