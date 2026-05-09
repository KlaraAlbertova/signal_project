package alerts.strategies;

import com.alerts.AlertManager;
import com.alerts.strategies.HeartRateStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeartRateStrategyTest {

    private AlertManager alertManager;
    private Patient patient;
    private HeartRateStrategy strategy;

    @BeforeEach
    void setUp() {
        alertManager = new AlertManager();
        patient = new Patient(1);
        strategy = new HeartRateStrategy();
    }

    @Test
    void testAbnormalSpikeTriggersAlert() {
        List<PatientRecord> records = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            records.add(new PatientRecord(1, 70.0, "ECG", i * 1000L));
        }
        records.add(new PatientRecord(1, 110.0, "ECG", 10000L)); // 70 * 1.5 = 105, so 110 triggers

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertTrue(alertManager.getDispatchedAlerts().get(0).getCondition().contains("ECG abnormal"));
    }

    @Test
    void testNormalReadingNoAlert() {
        List<PatientRecord> records = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            records.add(new PatientRecord(1, 70.0, "ECG", i * 1000L));
        }

        strategy.checkAlert(patient, records, alertManager);

        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testNotEnoughRecordsNoAlert() {
        List<PatientRecord> records = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            records.add(new PatientRecord(1, 70.0, "ECG", i * 1000L));
        }

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
