package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class PatientTest {

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient(1);
    }


    @Test
    void testAddOneRecord() {
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        assertEquals(1, patient.getAllRecords().size());
    }

    @Test
    void testAddMultipleRecords() {
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(80.0, "DiastolicPressure", 2000L);
        patient.addRecord(95.0, "Saturation", 3000L);
        assertEquals(3, patient.getAllRecords().size());
    }

    @Test
    void testAddedRecordHasCorrectValues() {
        patient.addRecord(120.0, "SystolicPressure", 5000L);
        PatientRecord record = patient.getAllRecords().get(0);
        assertEquals(120.0, record.getMeasurementValue());
        assertEquals("SystolicPressure", record.getRecordType());
        assertEquals(5000L, record.getTimestamp());
        assertEquals(1, record.getPatientId());
    }


    @Test
    void testGetRecordsInRange() {
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(110.0, "SystolicPressure", 2000L);
        patient.addRecord(120.0, "SystolicPressure", 3000L);

        List<PatientRecord> result = patient.getRecords(1000L, 2000L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetRecordsExcludesOutOfRange() {
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(110.0, "SystolicPressure", 5000L); // outside range

        List<PatientRecord> result = patient.getRecords(1000L, 2000L);
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsEmptyWhenNoMatch() {
        patient.addRecord(100.0, "SystolicPressure", 9000L);

        List<PatientRecord> result = patient.getRecords(1000L, 2000L);
        assertEquals(0, result.size());
    }

    @Test
    void testGetRecordsIncludesBoundaryValues() {
        // start and end timestamps should be included
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(110.0, "SystolicPressure", 5000L);

        List<PatientRecord> result = patient.getRecords(1000L, 5000L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetRecordsWhenNoRecordsAdded() {
        List<PatientRecord> result = patient.getRecords(1000L, 5000L);
        assertEquals(0, result.size());
    }


    @Test
    void testGetAllRecordsEmpty() {
        assertEquals(0, patient.getAllRecords().size());
    }

    @Test
    void testGetAllRecordsReturnsAll() {
        patient.addRecord(100.0, "SystolicPressure", 1000L);
        patient.addRecord(80.0, "DiastolicPressure", 2000L);

        assertEquals(2, patient.getAllRecords().size());
    }
}
