package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.AlertGenerator;
import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.alerts.alerts.BloodOxygenAlert;
import com.alerts.alerts.BloodPressureAlert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.data_management.patients.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class PatientDataFlowIntegrationTest {

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
    void testOxygenSaturationEndToEndFlow() {
        patient.addRecord(91.0, "Saturation", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());

        Alert alert = alerts.get(0);
        assertTrue(alert instanceof PriorityAlertDecorator);

        PriorityAlertDecorator priorityDecorator = (PriorityAlertDecorator) alert;
        assertEquals("HIGH", priorityDecorator.getPriorityLevel());
        assertEquals("[PRIORITY: HIGH] Blood oxygen saturation bellow 92%", priorityDecorator.getCondition());
    }

    @Test
    void testSystolicPressureEndToEndFlow() {
        patient.addRecord(190.0, "SystolicPressure", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());

        Alert alert = alerts.get(0);
        assertTrue(alert instanceof BloodPressureAlert);
        assertFalse(alert instanceof PriorityAlertDecorator);
        assertEquals("Systolic pressure exceeds 180 mmHg", alert.getCondition());
    }

    @Test
    void testManualAlertEndToEndFlow() {
        patient.addRecord(1.0, "Alert", 1000L);

        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertManager.getDispatchedAlerts();
        assertEquals(1, alerts.size());

        Alert alert = alerts.get(0);
        assertFalse(alert instanceof PriorityAlertDecorator);
        assertEquals("Manual alert triggered", alert.getCondition());
    }
}