package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

class PatientRecordTest {

    @Test
    void testGetPatientId() {
        PatientRecord record = new PatientRecord(1, 100.0, "SystolicPressure", 1000L);
        assertEquals(1, record.getPatientId());
    }

    @Test
    void testGetMeasurementValue() {
        PatientRecord record = new PatientRecord(1, 100.0, "SystolicPressure", 1000L);
        assertEquals(100.0, record.getMeasurementValue());
    }

    @Test
    void testGetRecordType() {
        PatientRecord record = new PatientRecord(1, 100.0, "SystolicPressure", 1000L);
        assertEquals("SystolicPressure", record.getRecordType());
    }

    @Test
    void testGetTimestamp() {
        PatientRecord record = new PatientRecord(1, 100.0, "SystolicPressure", 1000L);
        assertEquals(1000L, record.getTimestamp());
    }
}
