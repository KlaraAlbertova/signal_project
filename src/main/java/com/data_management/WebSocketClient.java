package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Connects to a WebSocket server and continuously receives patient data.
 *
 * <p>Each incoming message is parsed via {@link DataParser} and stored
 * in {@link DataStorage}. Malformed messages are logged and skipped so
 * one bad message never kills the stream.</p>
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final DataParser dataParser = new DataParser();
    private final DataStorage dataStorage;

    /**
     * Constructs a {@code WebSocketClient} that connects to the given server URI
     * and stores parsed records in the given {@link DataStorage}.
     *
     * @param serverUri   the WebSocket server URI
     * @param dataStorage where parsed records will be stored
     */
    public WebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server: " + getURI());
    }

    /**
     * Called automatically for every message from the server.
     * Parses as a WebSocket-formatted message and stores in DataStorage;
     * skips corrupted or empty messages.
     */
    @Override
    public void onMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            System.err.println("Received empty message, skipping.");
            return;
        }
        try {
            dataParser.parse(message.trim(), dataStorage, "WS");
        } catch (Exception e) {
            System.err.println("Failed to parse message: " + message);
            System.err.println("Reason: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Code: " + code
                + ", Reason: " + (reason.isEmpty() ? "none" : reason));
        if (remote) {
            System.err.println("Connection lost — server closed the connection.");
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }
}