package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.Alert;
import org.junit.jupiter.api.Test;

class AlertTest {

    @Test
    void testGetPatientId() {
        Alert alert = new Alert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        assertEquals("1", alert.getPatientId());
    }

    @Test
    void testGetCondition() {
        Alert alert = new Alert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        assertEquals("Systolic pressure exceeds 180 mmHg", alert.getCondition());
    }

    @Test
    void testGetTimestamp() {
        Alert alert = new Alert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        assertEquals(1000L, alert.getTimestamp());
    }
}