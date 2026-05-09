package alerts.decorators;


import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.data_management.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertDecoratorTest {

    private AlertManager alertManager;
    private Alert baseAlert;

    @BeforeEach
    void setUp() {
        alertManager = new AlertManager();
        baseAlert = new Alert("1", "ECG abnormal", 1000L);
    }

    // PriorityDecorator

    @Test
    void testPriorityDecoratorCondition() {
        PriorityAlertDecorator prioritized = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertEquals("[PRIORITY: HIGH] ECG abnormal", prioritized.getCondition());
    }

    @Test
    void testPriorityDecoratorPatientId() {
        PriorityAlertDecorator prioritized = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertEquals("1", prioritized.getPatientId());
    }

    @Test
    void testPriorityDecoratorTimestamp() {
        PriorityAlertDecorator prioritized = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertEquals(1000L, prioritized.getTimestamp());
    }

    @Test
    void testPriorityDecoratorGetPriorityLevel() {
        PriorityAlertDecorator prioritized = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertEquals("HIGH", prioritized.getPriorityLevel());
    }

    // RepeatedDecorator

    @Test
    void testRepeatedDecoratorRepeatCount() {
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(baseAlert, 3, 5000L);
        assertEquals(3, repeated.getRepeatCount());
    }

    @Test
    void testRepeatedDecoratorIntervalMs() {
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(baseAlert, 3, 5000L);
        assertEquals(5000L, repeated.getIntervalMs());
    }

    @Test
    void testRepeatedDecoratorDispatchesCorrectNumberOfTimes() {
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(baseAlert, 3, 0L);
        repeated.checkAndRepeat(alertManager, List.of(new Staff(0)));
        assertEquals(3, alertManager.getDispatchedAlerts().size());
    }

}
