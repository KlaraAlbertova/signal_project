package data_management;

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


class FileDataReaderIntegrationTest {

    @TempDir
    Path tempDir;

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
        storage = DataStorage.getInstance(new MockReader(""));
    }

    @Test
    void testTXTFileFlowsEndToEnd() throws IOException {
        Files.writeString(tempDir.resolve("patients.txt"),
                "Patient ID: 7, Timestamp: 1000, Label: HeartRate, Data: 68.0\n" +
                        "Patient ID: 7, Timestamp: 2000, Label: HeartRate, Data: 70.0\n");

        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(2, storage.getRecords(7, 0L, 9999L).size());
    }

    @Test
    void testUnsupportedCSVFileIsSkippedEndToEnd() throws IOException {
        Files.writeString(tempDir.resolve("patients.csv"),
                "5,98.6,Temperature,1000\n");

        new FileDataReader(tempDir.toString()).readData(storage);

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMixedGoodAndBadLinesInTXTOnlyGoodStored() throws IOException {
        Files.writeString(tempDir.resolve("mixed.txt"),
                "Patient ID: 8, Timestamp: 1000, Label: SystolicPressure, Data: 110.0\n" +
                        "NOT,VALID,DATA\n" +
                        "Patient ID: 8, Timestamp: 2000, Label: DiastolicPressure, Data: 70.0\n");

        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(2, storage.getRecords(8, 0L, 9999L).size());
    }

    @Test
    void testMultipleTXTFilesInDirectoryAllRead() throws IOException {
        Files.writeString(tempDir.resolve("a.txt"), "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 95.0\n");
        Files.writeString(tempDir.resolve("b.txt"), "Patient ID: 2, Timestamp: 2000, Label: SystolicPressure, Data: 120.0\n");

        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(1, storage.getRecords(1, 0L, 9999L).size());
        assertEquals(1, storage.getRecords(2, 0L, 9999L).size());
    }

    @Test
    void testNonExistentDirectoryThrowsAndStorageUntouched() {
        FileDataReader reader = new FileDataReader("/this/does/not/exist");
        assertThrows(IOException.class, () -> reader.readData(storage));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testTimestampRangeFilteringWorksAfterFileRead() throws IOException {
        Files.writeString(tempDir.resolve("data.txt"),
                "Patient ID: 10, Timestamp: 1000, Label: HeartRate, Data: 100.0\n" +
                        "Patient ID: 10, Timestamp: 3000, Label: HeartRate, Data: 105.0\n" +
                        "Patient ID: 10, Timestamp: 5000, Label: HeartRate, Data: 110.0\n");

        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> narrow = storage.getRecords(10, 2000L, 4000L);
        assertEquals(1, narrow.size());
        assertEquals(105.0, narrow.get(0).getMeasurementValue(), 0.001);
    }
}
