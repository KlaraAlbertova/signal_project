package data_management.dataReaders;

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

class WebSocketIntegrationTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
        storage = DataStorage.getInstance(new MockReader(""));
    }

    @Test
    void testSingleMessageFlowsEndToEnd() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);

        client.onMessage("1,120.0,SystolicPressure,1000");

        List<PatientRecord> records = storage.getRecords(1, 0L, 9999L);
        assertEquals(1, records.size());
        assertEquals(120.0, records.get(0).getMeasurementValue(), 0.001);
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(1000L, records.get(0).getTimestamp());
    }

    @Test
    void testHighVolumeMessagesAllStoredCorrectly() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);
        int count = 100;

        for (int i = 0; i < count; i++) {
            client.onMessage("1,72.0,HeartRate," + (1000 + i));
        }

        assertEquals(count, storage.getRecords(1, 0L, 999999L).size());
    }

    @Test
    void testBadMessagesInterleavedWithGoodPreserveGoodOnes() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);

        client.onMessage("2,80.0,DiastolicPressure,1000");
        client.onMessage("CORRUPT");
        client.onMessage("2,82.0,DiastolicPressure,2000");
        client.onMessage((String) null);
        client.onMessage("2,78.0,DiastolicPressure,3000");

        assertEquals(3, storage.getRecords(2, 0L, 9999L).size());
    }

    @Test
    void testTimestampRangeFilteringWorksEndToEnd() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);

        client.onMessage("3,100.0,SystolicPressure,1000");
        client.onMessage("3,95.0,SystolicPressure,2000");
        client.onMessage("3,90.0,SystolicPressure,3000");

        List<PatientRecord> narrow = storage.getRecords(3, 1500L, 2500L);
        assertEquals(1, narrow.size());
        assertEquals(95.0, narrow.get(0).getMeasurementValue(), 0.001);
    }

    @Test
    void testOnErrorLeavesAlreadyStoredDataIntact() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);
        client.onMessage("9,65.0,HeartRate,1000");

        client.onError(new Exception("Network failure"));

        assertEquals(1, storage.getRecords(9, 0L, 9999L).size());
    }

    @Test
    void testOnCloseLeavesAlreadyStoredDataIntact() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);
        client.onMessage("11,88.0,HeartRate,5000");

        client.onClose(1000, "Normal", false);

        assertEquals(1, storage.getRecords(11, 0L, 9999L).size());
    }

    @Test
    void testReconnectWithNewClientContinuesStoringToSameStorage() throws URISyntaxException {
        WebSocketClient client1 = new WebSocketClient(new URI("ws://localhost:9090"), storage);
        client1.onMessage("12,100.0,SystolicPressure,1000");
        client1.onClose(1006, "Lost", true);

        WebSocketClient client2 = new WebSocketClient(new URI("ws://localhost:9090"), storage);
        client2.onMessage("12,102.0,SystolicPressure,2000");

        assertEquals(2, storage.getRecords(12, 0L, 9999L).size());
    }

    @Test
    void testMultiplePatientsAcrossMultipleMessages() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:9090"), storage);

        client.onMessage("1,95.0,HeartRate,1000");
        client.onMessage("2,120.0,SystolicPressure,1000");
        client.onMessage("3,98.6,Temperature,1000");

        assertEquals(1, storage.getRecords(1, 0L, 9999L).size());
        assertEquals(1, storage.getRecords(2, 0L, 9999L).size());
        assertEquals(1, storage.getRecords(3, 0L, 9999L).size());
        assertEquals(3, storage.getAllPatients().size());
    }
}
