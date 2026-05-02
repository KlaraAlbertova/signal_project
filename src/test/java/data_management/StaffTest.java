package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.Alert;
import com.data_management.PatientRecord;
import com.data_management.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StaffTest {
    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff(1);
    }

    @Test
    void testAddOneAlert() {
        staff.addAlert(new Alert("1", "Manual alert triggered", 1000L));
        assertEquals(1, staff.getAlerts().size());
    }

    @Test
    void testAddMultipleAlert() {
        staff.addAlert(new Alert("1", "Manual alert triggered", 1000L));
        staff.addAlert(new Alert("2", "Manual alert triggered", 2000L));
        staff.addAlert(new Alert("3", "Manual alert triggered", 3000L));
        assertEquals(3, staff.getAlerts().size());
    }

    @Test
    void testGetStaffID() {
        assertEquals(1, staff.getStaffId());
    }

    @Test
    void testAddedAlertHasCorrectValues() {
        staff.addAlert(new Alert("1", "Manual alert triggered", 1000L));
        Alert alert = staff.getAlerts().get(0);
        assertEquals("1", alert.getPatientId());
        assertEquals("Manual alert triggered", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }
}
