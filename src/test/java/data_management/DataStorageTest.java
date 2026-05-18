package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.dataReaders.DataReader;
import com.data_management.dataReaders.FileDataReader;
import com.data_management.dataReaders.MockReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.data_management.DataStorage;
import com.data_management.patients.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class DataStorageTest {

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

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

    @Test
    void testFileDataReaderLoadsRecordsIntoStorage(@TempDir Path tempDir) throws IOException {
        // Write a sample file matching FileOutputStrategy's TXT format
        Path file = tempDir.resolve("HeartRate.txt");
        Files.writeString(file,
                "Patient ID: 1, Timestamp: 1714376789050, Label: HeartRate, Data: 72.0\n" +
                        "Patient ID: 1, Timestamp: 1714376789051, Label: HeartRate, Data: 75.0\n"
        );

        DataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = DataStorage.getInstance(reader);
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size());
        assertEquals(72.0, records.get(0).getMeasurementValue(), 0.001);
        assertEquals("HeartRate", records.get(0).getRecordType());
    }
}