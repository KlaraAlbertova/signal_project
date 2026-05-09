package alerts.factories;

import com.alerts.alerts.Alert;
import com.alerts.alerts.BloodOxygenAlert;
import com.alerts.alerts.BloodPressureAlert;
import com.alerts.alerts.ECGAlert;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertFactoryTest {

    // BloodPressureAlertFactory

    @Test
    void testBloodPressureAlertFactoryCreatesCorrectType() {
        Alert alert = new BloodPressureAlertFactory().createAlert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        assertInstanceOf(BloodPressureAlert.class, alert);
    }

    @Test
    void testBloodPressureAlertFactoryFields() {
        Alert alert = new BloodPressureAlertFactory().createAlert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("Systolic pressure exceeds 180 mmHg", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }

    // BloodOxygenAlertFactory

    @Test
    void testBloodOxygenAlertFactoryCreatesCorrectType() {
        Alert alert = new BloodOxygenAlertFactory().createAlert("1", "Blood oxygen saturation bellow 92%", 2000L);
        assertInstanceOf(BloodOxygenAlert.class, alert);
    }

    @Test
    void testBloodOxygenAlertFactoryFields() {
        Alert alert = new BloodOxygenAlertFactory().createAlert("1", "Blood oxygen saturation bellow 92%", 2000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("Blood oxygen saturation bellow 92%", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
    }

    // ECGAlertFactory

    @Test
    void testECGAlertFactoryCreatesCorrectType() {
        Alert alert = new ECGAlertFactory().createAlert("1", "ECG abnormal", 3000L);
        assertInstanceOf(ECGAlert.class, alert);
    }

    @Test
    void testECGAlertFactoryFields() {
        Alert alert = new ECGAlertFactory().createAlert("1", "ECG abnormal", 3000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("ECG abnormal", alert.getCondition());
        assertEquals(3000L, alert.getTimestamp());
    }
}
