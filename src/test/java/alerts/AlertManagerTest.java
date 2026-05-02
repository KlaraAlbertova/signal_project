package alerts;


import static org.junit.jupiter.api.Assertions.*;

import com.alerts.Alert;
import com.alerts.AlertManager;
import com.data_management.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlertManagerTest {

    private AlertManager alertManager;

    @BeforeEach
    void setUp() {
        alertManager = new AlertManager();
    }

    @Test
    void testDispatchedAlertsEmptyAtStart() {
        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testDispatchAlertStoresAlert() {
        Alert alert = new Alert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        Staff staff = new Staff(1);
        alertManager.dispatchAlert(alert, List.of(staff));

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertEquals("Systolic pressure exceeds 180 mmHg", alertManager.getDispatchedAlerts().get(0).getCondition());
    }

    @Test
    void testDispatchMultipleAlerts() {
        Alert alert1 = new Alert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        Alert alert2 = new Alert("1", "Blood saturation bellow 92%", 2000L);
        Staff staff = new Staff(1);

        alertManager.dispatchAlert(alert1, List.of(staff));
        alertManager.dispatchAlert(alert2, List.of(staff));

        assertEquals(2, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testDispatchAlertNotifiesStaff() {
        Alert alert = new Alert("1", "Systolic pressure exceeds 180 mmHg", 1000L);
        Staff staff = new Staff(1);

        alertManager.dispatchAlert(alert, List.of(staff));

        assertEquals(1, staff.getAlerts().size());
        assertEquals("Systolic pressure exceeds 180 mmHg", staff.getAlerts().get(0).getCondition());
    }
}
