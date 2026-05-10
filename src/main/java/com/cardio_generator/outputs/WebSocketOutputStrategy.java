package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    /**
     * Formats and broadcasts a patient data record to all currently connected WebSocket clients.
     *
     * <p>The message is formatted as a comma-separated string with four fields:</p>
     * <pre>patientId,data,label,timestamp</pre>
     *
     * <p>If any required field is invalid — {@code patientId} or {@code timestamp} is
     * non-positive, or {@code label} or {@code data} is {@code null} or empty — the
     * output is skipped and a warning is printed to {@code System.err}.</p>
     *
     * <p>Each connected client is messaged individually. If sending to a particular
     * client fails, the error is logged and broadcasting continues to the remaining
     * clients so one bad connection never silences the rest.</p>
     *
     * @param patientId  the unique identifier of the patient; must be greater than zero
     * @param timestamp  the time of the measurement in milliseconds since the Unix epoch;
     *                   must be greater than zero
     * @param label      the type of measurement (e.g., {@code "HeartRate"}); must not be
     *                   {@code null} or empty
     * @param data       the measurement value as a string (e.g., {@code "72.5"}); must not
     *                   be {@code null} or empty
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (patientId <= 0 || timestamp <= 0 || label == null || label.isEmpty() || data == null || data.isEmpty()) {
            System.err.println("Skipping output: invalid or missing patient data.");
            return;
        }

        String message = String.format("%d,%s,%s,%d", patientId, data, label, timestamp);

        for (WebSocket conn : server.getConnections()) {
            try {
                conn.send(message);
            } catch (Exception e) {
                System.err.println("Failed to send message to client "
                        + conn.getRemoteSocketAddress() + ": " + e.getMessage());
            }
        }
    }


    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
