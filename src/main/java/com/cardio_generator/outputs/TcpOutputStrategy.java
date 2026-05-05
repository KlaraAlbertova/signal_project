package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link OutputStrategy} that streams health data over a TCP network socket.
 * This class initializes a server on a given port and accepts a single client connection.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a {@code TcpOutputStrategy} and starts a TCP server on the specified port.
     * The client connection is accepted on a separate thread so as not to block the main thread.
     *
     * @param port the network port to listen on
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends patient data to the connected TCP client.
     * The data is transmitted in the format:
     * {@code [patientId],[timestamp],[label],[data]}.
     * If no client is connected, the output is silently skipped.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the type of data being recorded
     * @param data      the data value to be transmitted
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}