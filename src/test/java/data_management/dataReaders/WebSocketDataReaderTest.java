package data_management.dataReaders;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataStorage;
import com.data_management.dataReaders.MockReader;
import com.data_management.dataReaders.WebSocketDataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class WebSocketDataReaderTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
        storage = DataStorage.getInstance(new MockReader(""));
    }

    @Test
    void testReadDataInitializesClient() {
        WebSocketDataReader reader = new WebSocketDataReader("localhost", 8080);
        assertDoesNotThrow(() -> reader.readData(storage));
    }

    @Test
    void testReadDataWithInvalidUriThrowsIOException() {
        WebSocketDataReader reader = new WebSocketDataReader("invalid host name", 8080);
        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testDisconnectBeforeConnection() {
        WebSocketDataReader reader = new WebSocketDataReader("localhost", 8080);
        assertDoesNotThrow(() -> reader.disconnect());
    }

    @Test
    void testDisconnectAfterConnection() throws IOException {
        WebSocketDataReader reader = new WebSocketDataReader("localhost", 8080);
        reader.readData(storage);
        assertDoesNotThrow(() -> reader.disconnect());
    }
}