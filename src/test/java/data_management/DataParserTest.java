package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataParser;
import com.data_management.DataStorage;
import com.data_management.MockReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DataParserTest {

    private DataParser dataParser;
    private DataStorage storage;

    @BeforeEach
    void setUp() {
        dataParser = new DataParser();
        storage = new DataStorage(new MockReader(""));
    }

    @Test
    void testValidCSVLineIsParsed() {
        dataParser.parse("1,100.0,SystolicPressure,1000", storage, "CSV");

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(1000L, records.get(0).getTimestamp());
    }

    @Test
    void testNullDataDoesNothing() {
        dataParser.parse(null, storage, "CSV");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testEmptyDataDoesNothing() {
        dataParser.parse("", storage, "CSV");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testNullStorageDoesNothing() {
        assertDoesNotThrow(() -> dataParser.parse("1,100.0,SystolicPressure,1000", null, "CSV"));
    }

    @Test
    void testNullFormatDoesNothing() {
        dataParser.parse("1,100.0,SystolicPressure,1000", storage, null);
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testEmptyFormatDoesNothing() {
        dataParser.parse("1,100.0,SystolicPressure,1000", storage, "");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testUnsupportedFormatDoesNothing() {
        dataParser.parse("1,100.0,SystolicPressure,1000", storage, "XML");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testMalformedCSVLineIsSkipped() {
        dataParser.parse("this is not valid", storage, "CSV");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testCSVWithWrongNumberOfColumnsIsSkipped() {
        // only 3 columns instead of 4
        dataParser.parse("1,100.0,SystolicPressure", storage, "CSV");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testCSVWithNonNumericValuesIsSkipped() {
        dataParser.parse("abc,100.0,SystolicPressure,1000", storage, "CSV");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }
}
