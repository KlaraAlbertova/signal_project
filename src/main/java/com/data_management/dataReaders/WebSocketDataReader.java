package com.data_management.dataReaders;

import com.data_management.DataStorage;
import com.data_management.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implements {@link DataReader} for real-time data via a WebSocket connection.
 *
 * <p>Connects to a running WebSocket server and continuously receives patient
 * data messages via {@link WebSocketClient}, parsing each one and storing
 * the result in {@link DataStorage}.</p>
 *
 * <p>The connection is non-blocking: {@link #readData} starts the client and
 * returns immediately. Data keeps arriving at the WebSocket thread until
 * {@link #disconnect} is called or the server closes the connection.</p>
 */
public class WebSocketDataReader implements DataReader {

    private final String serverUri;
    private WebSocketClient client;

    /**
     * Constructs a {@code WebSocketDataReader} that connects to the WebSocket
     * server at the given host and port.
     *
     * @param host the WebSocket server host
     * @param port the WebSocket server port
     */
    public WebSocketDataReader(String host, int port) {
        this.serverUri = "ws://" + host + ":" + port;
    }

    /**
     * Creates a {@link WebSocketClient}, connects it to the server,
     * and starts receiving data asynchronously.
     *
     * @param dataStorage where parsed records are stored
     * @throws IOException if the URI is malformed or the connection fails
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try {
            client = new WebSocketClient(new URI(serverUri), dataStorage);
            client.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI: " + serverUri, e);
        }
    }

    /**
     * Closes the WebSocket connection gracefully.
     * Safe to call even if the client was never connected.
     */
    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}