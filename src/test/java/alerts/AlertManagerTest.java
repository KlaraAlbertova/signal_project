package alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.alerts.Alert;
import com.alerts.AlertManager;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.data_management.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class AlertManagerTest {

    private AlertManager alertManager;
    private List<Staff> staffMembers;
    private Staff staff;

    @BeforeEach
    void setUp() {
        alertManager = new AlertManager();
        staff = new Staff(1);
        staffMembers = new ArrayList<>();
        staffMembers.add(staff);
    }

    @Test
    void testDispatchedAlertsEmptyAtStart() {
        assertEquals(0, alertManager.getDispatchedAlerts().size());
    }

    @Test
    void testDispatchStandardAlert() {
        Alert alert = new Alert("1", "Normal Condition", 1000L);
        alertManager.dispatchAlert(alert, staffMembers);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        assertFalse(alertManager.getDispatchedAlerts().get(0) instanceof PriorityAlertDecorator);
        assertEquals(1, staff.getAlerts().size());
    }

    @Test
    void testDispatchHighPriorityAlertBellow92() {
        Alert alert = new Alert("1", "Blood saturation bellow 92%", 1000L);
        alertManager.dispatchAlert(alert, staffMembers);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        Alert dispatched = alertManager.getDispatchedAlerts().get(0);
        assertTrue(dispatched instanceof PriorityAlertDecorator);
        assertEquals("[PRIORITY: HIGH] Blood saturation bellow 92%", dispatched.getCondition());
    }

    @Test
    void testDispatchHighPriorityAlertRapidDrop() {
        Alert alert = new Alert("1", "Rapid drop in heart rate", 1000L);
        alertManager.dispatchAlert(alert, staffMembers);

        assertEquals(1, alertManager.getDispatchedAlerts().size());
        Alert dispatched = alertManager.getDispatchedAlerts().get(0);
        assertTrue(dispatched instanceof PriorityAlertDecorator);
        assertEquals("[PRIORITY: HIGH] Rapid drop in heart rate", dispatched.getCondition());
    }

    @Test
    void testPriorityDecoratorGetters() {
        Alert alert = new Alert("1", "Test", 1000L);
        PriorityAlertDecorator decorator = new PriorityAlertDecorator(alert, "URGENT");
        assertEquals("URGENT", decorator.getPriorityLevel());
        assertEquals("[PRIORITY: URGENT] Test", decorator.getCondition());
    }

    @Test
    void testRepeatedAlertDecoratorGetters() {
        Alert alert = new Alert("1", "Test", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(alert, 3, 5000);
        assertEquals(3, repeated.getRepeatCount());
        assertEquals(5000, repeated.getIntervalMs());
    }
}