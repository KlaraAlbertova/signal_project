package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.MockReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileDataReaderTest {

    @TempDir
    Path tempDir; // JUnit creates a temporary folder

    @Test
    void testReadValidCSVFile() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "1,100.0,SystolicPressure,1000\n");

        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage(new MockReader(""));
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
    }

    @Test
    void testReadMultipleLines() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "1,100.0,SystolicPressure,1000\n1,80.0,DiastolicPressure,2000\n");

        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage(new MockReader(""));
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(2, records.size());
    }

    @Test
    void testInvalidDirectoryThrowsIOException() {
        FileDataReader reader = new FileDataReader("nonexistent/path");
        DataStorage storage = new DataStorage(new MockReader(""));

        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testMalformedLineIsSkipped() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        // one bad, one good
        Files.writeString(csvFile, "this is not valid csv\n1,100.0,SystolicPressure,1000\n");

        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage(new MockReader(""));
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
    }

    @Test
    void testNonCSVFileIsSkipped() throws IOException {
        Path txtFile = tempDir.resolve("test.txt");
        Files.writeString(txtFile, "1,100.0,SystolicPressure,1000\n");

        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage(new MockReader(""));
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(0, records.size());
    }

    @Test
    void testEmptyCSVFileNoRecords() throws IOException {
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "");

        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage(new MockReader(""));
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(0, records.size());
    }
}
