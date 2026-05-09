package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataReader;
import com.data_management.MockReader;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        DataReader reader = new MockReader("path/to/data");
        DataStorage storage = DataStorage.getInstance(reader);
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetInstanceReturnsSameInstance() {
        DataStorage instance1 = DataStorage.getInstance(new MockReader("path/to/data"));
        DataStorage instance2 = DataStorage.getInstance(new MockReader("path/to/data"));
        assertSame(instance1, instance2);
    }

    @Test
    void testGetInstanceNotNull() {
        DataStorage instance = DataStorage.getInstance(new MockReader("path/to/data"));
        assertNotNull(instance);
    }

    @Test
    void testGetRecordsEmptyWhenNoPatient() {
        DataStorage storage = DataStorage.getInstance(new MockReader("path/to/data"));
        List<PatientRecord> records = storage.getRecords(999, 1714376789050L, 1714376789051L);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetAllPatients() {
        DataStorage storage = DataStorage.getInstance(new MockReader("path/to/data"));
        storage.addPatientData(2, 100.0, "WhiteBloodCells", 1714376789050L);
        assertFalse(storage.getAllPatients().isEmpty());
    }
}
