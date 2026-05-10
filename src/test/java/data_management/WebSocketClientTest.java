package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.WebSocketClient;
import com.data_management.dataReaders.MockReader;
import com.data_management.patients.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

class WebSocketClientTest {

    private DataStorage storage;
    private WebSocketClient client;

    @BeforeEach
    void setUp() throws URISyntaxException {
        DataStorage.resetInstance();
        storage = DataStorage.getInstance(new MockReader(""));
        client = new WebSocketClient(new URI("ws://localhost:9090"), storage);
    }


    @Test
    void testValidMessageIsStoredInDataStorage() {
        client.onMessage("1,98.6,Temperature,1000");

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(98.6,          records.get(0).getMeasurementValue(), 0.001);
        assertEquals("Temperature", records.get(0).getRecordType());
        assertEquals(1000L,         records.get(0).getTimestamp());
    }

    @Test
    void testMultipleValidMessagesAreAllStored() {
        client.onMessage("1,120.0,SystolicPressure,1000");
        client.onMessage("1,80.0,DiastolicPressure,2000");
        client.onMessage("1,72.0,HeartRate,3000");

        assertEquals(3, storage.getRecords(1, 0L, 9999L).size());
    }

    @Test
    void testMessagesForDifferentPatientsStoredSeparately() {
        client.onMessage("10,100.0,SystolicPressure,1000");
        client.onMessage("20,60.0,HeartRate,2000");

        assertEquals(1, storage.getRecords(10, 0L, 9999L).size());
        assertEquals(1, storage.getRecords(20, 0L, 9999L).size());
        assertEquals(2, storage.getAllPatients().size());
    }

    @Test
    void testMessageWithLeadingAndTrailingWhitespaceIsParsed() {
        client.onMessage("   3,75.0,HeartRate,5000   ");

        List<PatientRecord> records = storage.getRecords(3, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(75.0, records.get(0).getMeasurementValue(), 0.001);
    }

    // errors

    @Test
    void testNullMessageIsSkippedGracefully() {
        assertDoesNotThrow(() -> client.onMessage((String) null));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testEmptyMessageIsSkippedGracefully() {
        assertDoesNotThrow(() -> client.onMessage(""));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testBlankWhitespaceOnlyMessageIsSkipped() {
        assertDoesNotThrow(() -> client.onMessage("   "));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMalformedMessageIsSkippedAndNoRecordStored() {
        assertDoesNotThrow(() -> client.onMessage("this is not valid data"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMessageWithTooFewFieldsIsSkipped() {
        assertDoesNotThrow(() -> client.onMessage("1,100.0,HeartRate"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMessageWithTooManyFieldsIsSkipped() {
        assertDoesNotThrow(() -> client.onMessage("1,100.0,HeartRate,1000,extra"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMessageWithNonNumericPatientIdIsSkipped() {
        assertDoesNotThrow(() -> client.onMessage("abc,100.0,HeartRate,1000"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMessageWithNonNumericTimestampIsSkipped() {
        assertDoesNotThrow(() -> client.onMessage("1,100.0,HeartRate,notATimestamp"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testMessageWithNonNumericValueIsSkipped() {
        assertDoesNotThrow(() -> client.onMessage("1,notADouble,HeartRate,1000"));
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testOneGoodOneBadMessageOnlyGoodIsStored() {
        client.onMessage("BadMessage");
        client.onMessage("1,90.0,BloodSaturation,2000");

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(90.0, records.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testStorageRemainsUsableAfterFloodOfBadMessages() {
        for (int i = 0; i < 10; i++) {
            client.onMessage("BadMessage" + i);
        }
        client.onMessage("99,55.0,Cholesterol,8000");

        List<PatientRecord> records = storage.getRecords(99, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(55.0, records.get(0).getMeasurementValue(), 0.001);
    }


    @Test
    void testOnCloseNormalDoesNotThrow() {
        assertDoesNotThrow(() -> client.onClose(1000, "Normal closure", false));
    }

    @Test
    void testOnCloseRemoteDoesNotThrow() {
        assertDoesNotThrow(() -> client.onClose(1006, "Connection lost", true));
    }

    @Test
    void testOnCloseEmptyReasonDoesNotThrow() {
        assertDoesNotThrow(() -> client.onClose(1000, "", false));
    }

    @Test
    void testOnErrorDoesNotThrow() {
        assertDoesNotThrow(() -> client.onError(new Exception("Simulated network error")));
    }

    @Test
    void testOnErrorWithNullMessageDoesNotThrow() {
        assertDoesNotThrow(() -> client.onError(new RuntimeException((String) null)));
    }
}
