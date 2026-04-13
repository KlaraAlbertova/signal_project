package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implementation of the {@link OutputStrategy} that streams health data over a TCP network socket.
 * This class initializes a server on a given port.
 */

public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a TcpOutputStrategy with a specified port.
     * Starts a server on the given port.
     * The server runs on a new thread, so it would not block the main thread.
     *
     * @param port int. The network port to listen on.
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
     * Sends the patient data to the connected client.
     * The data is outputted in the format:
     * "[patientId],[timestamp],[label],[data]"
     *
     * @param patientId int. The ID of the patient.
     * @param timestamp long. The time the data was recorded.
     * @param label String. The type of data being recorded.
     * @param data String. The data to be stored.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
