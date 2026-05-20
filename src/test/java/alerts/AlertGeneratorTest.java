package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.AlertGenerator;
import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.data_management.patients.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AlertGeneratorTest {

    private AlertGenerator alertGenerator;
    private AlertManager alertManager;
    private Patient patient;

    @BeforeEach
    void setUp() {
        alertGenerator = new AlertGenerator(null);
        alertManager = alertGenerator.getAlertManager();
        patient = new Patient(1);
    }

    @Test
    void testHypotensiveHypoxemiaOnlyLowSystolicNoAlert() {
        patient.addRecord(85.0, "SystolicPressure", 1000L);
        patient.addRecord(95.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        boolean hasHypotensiveAlert = alerts.stream()
                .anyMatch(a -> a.getCondition().contains("Hypotensive Hypoxemia Alert"));

        assertFalse(hasHypotensiveAlert);
    }

    @Test
    void testHypotensiveHypoxemiaOnlyLowSaturationNoAlert() {
        patient.addRecord(120.0, "SystolicPressure", 1000L);
        patient.addRecord(90.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        boolean hasHypotensiveAlert = alerts.stream()
                .anyMatch(a -> a.getCondition().contains("Hypotensive Hypoxemia Alert"));

        assertFalse(hasHypotensiveAlert);
    }

    @Test
    void testHypotensiveHypoxemiaMismatchedTimestampsNoAlert() {
        patient.addRecord(85.0, "SystolicPressure", 1000L);
        patient.addRecord(90.0, "Saturation", 2000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        boolean hasHypotensiveAlert = alerts.stream()
                .anyMatch(a -> a.getCondition().contains("Hypotensive Hypoxemia Alert"));

        assertFalse(hasHypotensiveAlert);
    }

    @Test
    void testManualAlertTriggeredWhenValueIsOne() {
        patient.addRecord(1.0, "Alert", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());
        assertEquals("Manual alert triggered", alerts.get(0).getCondition());
        assertEquals("1", alerts.get(0).getPatientId());
        assertEquals(1000L, alerts.get(0).getTimestamp());
    }

    @Test
    void testManualAlertNotTriggeredWhenValueIsZero() {
        patient.addRecord(0.0, "Alert", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(0, alerts.size());
    }

    @Test
    void testEvaluateDataWithNoRecords() {
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(0, alerts.size());
    }
}