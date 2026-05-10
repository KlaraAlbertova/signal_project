package data_management.dataReaders;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.dataReaders.FileDataReader;
import com.data_management.dataReaders.MockReader;
import com.data_management.patients.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileDataReaderTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
    }

    @Test
    void testReadValidTXTFileSingleLine() throws IOException {
        Files.writeString(tempDir.resolve("data.txt"),
                "Patient ID: 1, Timestamp: 1000, Label: SystolicPressure, Data: 100.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue(), 0.001);
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(1000L, records.get(0).getTimestamp());
    }

    @Test
    void testReadValidTXTFileMultipleLines() throws IOException {
        Files.writeString(tempDir.resolve("data.txt"),
                "Patient ID: 1, Timestamp: 1000, Label: SystolicPressure, Data: 100.0\n" +
                        "Patient ID: 1, Timestamp: 2000, Label: DiastolicPressure, Data: 80.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(2, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testReadTXTFileMultiplePatientsStoredSeparately() throws IOException {
        Files.writeString(tempDir.resolve("data.txt"),
                "Patient ID: 10, Timestamp: 1000, Label: HeartRate, Data: 72.0\n" +
                        "Patient ID: 20, Timestamp: 2000, Label: HeartRate, Data: 68.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(1, storage.getRecords(10, 0L, 9999L).size());
        assertEquals(1, storage.getRecords(20, 0L, 9999L).size());
        assertEquals(2, storage.getAllPatients().size());
    }

    @Test
    void testReadMultipleTXTFilesInDirectory() throws IOException {
        Files.writeString(tempDir.resolve("a.txt"),
                "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 72.0\n");
        Files.writeString(tempDir.resolve("b.txt"),
                "Patient ID: 2, Timestamp: 2000, Label: SystolicPressure, Data: 120.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(1, storage.getRecords(1, 0L, 9999L).size());
        assertEquals(1, storage.getRecords(2, 0L, 9999L).size());
    }

    // errors

    @Test
    void testInvalidDirectoryThrowsIOException() {
        FileDataReader reader = new FileDataReader("nonexistent/path");
        DataStorage storage = DataStorage.getInstance(new MockReader(""));

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testMalformedLineIsSkippedGoodLineStored() throws IOException {
        Files.writeString(tempDir.resolve("data.txt"),
                "this is not valid\n" +
                        "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 72.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(1, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testEmptyTXTFileProducesNoRecords() throws IOException {
        Files.writeString(tempDir.resolve("empty.txt"), "");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testCSVFileIsSkipped() throws IOException {
        // .csv is not supported by FileDataReader — all lines must be ignored
        Files.writeString(tempDir.resolve("data.csv"),
                "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 72.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testXMLFileIsSkipped() throws IOException {
        Files.writeString(tempDir.resolve("data.xml"),
                "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 72.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testOnlyTXTFilesReadWhenMixedExtensionsPresent() throws IOException {
        Files.writeString(tempDir.resolve("good.txt"),
                "Patient ID: 5, Timestamp: 1000, Label: HeartRate, Data: 75.0\n");
        Files.writeString(tempDir.resolve("ignored.csv"),
                "Patient ID: 9, Timestamp: 2000, Label: HeartRate, Data: 80.0\n");

        DataStorage storage = DataStorage.getInstance(new MockReader(""));
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(1, storage.getRecords(5, 0L, 9999L).size());
        assertTrue(storage.getRecords(9, 0L, 9999L).isEmpty());
        assertEquals(1, storage.getAllPatients().size());
    }
}
