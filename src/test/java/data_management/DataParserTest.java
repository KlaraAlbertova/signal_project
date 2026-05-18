package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataParser;
import com.data_management.DataStorage;
import com.data_management.dataReaders.MockReader;
import com.data_management.patients.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DataParserTest {

    private DataParser dataParser;
    private DataStorage storage;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
        dataParser = new DataParser();
        storage = DataStorage.getInstance(new MockReader(""));
    }


    @Test
    void testValidTXTLineIsParsed() {
        dataParser.parse("Patient ID: 1, Timestamp: 1000, Label: SystolicPressure, Data: 100.0", storage, "TXT");

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue(), 0.001);
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(1000L, records.get(0).getTimestamp());
    }

    @Test
    void testTXTMultipleRecordsSamePatient() {
        dataParser.parse("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 70.0", storage, "TXT");
        dataParser.parse("Patient ID: 1, Timestamp: 2000, Label: HeartRate, Data: 72.0", storage, "TXT");
        dataParser.parse("Patient ID: 1, Timestamp: 3000, Label: HeartRate, Data: 74.0", storage, "TXT");

        assertEquals(3, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testTXTMalformedLineIsSkipped() {
        dataParser.parse("this is not valid", storage, "TXT");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testTXTWithTooFewFieldsIsSkipped() {
        dataParser.parse("Patient ID: 1, Timestamp: 1000, Label: HeartRate", storage, "TXT");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testTXTWithNonNumericPatientIdIsSkipped() {
        dataParser.parse("Patient ID: abc, Timestamp: 1000, Label: HeartRate, Data: 72.0", storage, "TXT");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testTXTWithNonNumericTimestampIsSkipped() {
        dataParser.parse("Patient ID: 1, Timestamp: notALong, Label: HeartRate, Data: 72.0", storage, "TXT");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testTXTWithNonNumericDataIsSkipped() {
        dataParser.parse("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: notADouble", storage, "TXT");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }


    @Test
    void testValidWSLineIsParsed() {
        dataParser.parse("2,98.6,Temperature,2000", storage, "WS");

        List<PatientRecord> records = storage.getRecords(2, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(98.6, records.get(0).getMeasurementValue(), 0.001);
        assertEquals("Temperature", records.get(0).getRecordType());
        assertEquals(2000L, records.get(0).getTimestamp());
    }

    @Test
    void testWSMultipleRecordsSamePatient() {
        dataParser.parse("3,80.0,DiastolicPressure,1000", storage, "WS");
        dataParser.parse("3,82.0,DiastolicPressure,2000", storage, "WS");
        dataParser.parse("3,78.0,DiastolicPressure,3000", storage, "WS");

        assertEquals(3, storage.getRecords(3, 0L, 9999L).size());
    }

    @Test
    void testWSWithLeadingTrailingWhitespaceIsParsed() {
        dataParser.parse("  4 , 110.0 , BloodSaturation , 4000  ", storage, "WS");

        List<PatientRecord> records = storage.getRecords(4, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(110.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testWSMalformedLineIsSkipped() {
        dataParser.parse("this is not valid", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testWSWithTooFewFieldsIsSkipped() {
        dataParser.parse("1,100.0,HeartRate", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testWSWithTooManyFieldsIsSkipped() {
        dataParser.parse("1,100.0,HeartRate,1000,extra", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testWSWithNonNumericPatientIdIsSkipped() {
        dataParser.parse("abc,100.0,HeartRate,1000", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testWSWithNonNumericTimestampIsSkipped() {
        dataParser.parse("1,100.0,HeartRate,notATimestamp", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testWSWithNonNumericMeasurementValueIsSkipped() {
        dataParser.parse("1,notADouble,HeartRate,1000", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }


    @Test
    void testNullDataDoesNothing() {
        dataParser.parse(null, storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testEmptyDataDoesNothing() {
        dataParser.parse("", storage, "WS");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testNullStorageDoesNotThrow() {
        assertDoesNotThrow(() -> dataParser.parse("1,100.0,HeartRate,1000", null, "WS"));
    }

    @Test
    void testNullFormatDoesNothing() {
        dataParser.parse("1,100.0,HeartRate,1000", storage, null);
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testEmptyFormatDoesNothing() {
        dataParser.parse("1,100.0,HeartRate,1000", storage, "");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testUnsupportedFormatDoesNothing() {
        dataParser.parse("1,100.0,HeartRate,1000", storage, "CSV");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testAnotherUnsupportedFormatDoesNothing() {
        dataParser.parse("1,100.0,HeartRate,1000", storage, "XML");
        assertEquals(0, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testTXTSaturationWithPercentIsParsed() {
        dataParser.parse(
                "Patient ID: 1, Timestamp: 1000, Label: Saturation, Data: 97%",
                storage, "TXT");
        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(97.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testTXTAlertTriggeredIsParsed() {
        dataParser.parse(
                "Patient ID: 1, Timestamp: 1000, Label: Alert, Data: triggered",
                storage, "TXT");
        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testTXTAlertResolvedIsParsed() {
        dataParser.parse(
                "Patient ID: 1, Timestamp: 1000, Label: Alert, Data: resolved",
                storage, "TXT");
        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(0.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testWSSaturationWithPercentIsParsed() {
        dataParser.parse("1,97%,Saturation,1000", storage, "WS");
        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(97.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testWSAlertTriggeredIsParsed() {
        dataParser.parse("1,triggered,Alert,1000", storage, "WS");
        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testWSAlertResolvedIsParsed() {
        dataParser.parse("1,resolved,Alert,1000", storage, "WS");
        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(0.0, records.get(0).getMeasurementValue(), 0.001);
    }
}
