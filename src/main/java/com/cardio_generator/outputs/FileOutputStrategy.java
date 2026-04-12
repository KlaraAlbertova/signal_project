package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// Change: added Javadoc comment
/**
 * Implementation of OutputStrategy.
 */
public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name from BaseDirectory, variables use lowerCamelCase
    // Change: Added final
    private final String baseDirectory;

    // Changed variable name from file_map, variables use lowerCamelCase
    // Changed from public to private
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    // Change: added Javadoc comment
    /**
     * Constructor for FileOutputStrategy.
     *
     * @param baseDirectory The base directory where output files will be stored.
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    // Changed variable name from timestamp, variables use lowerCamelCase
    // Change: added Javadoc comment
    /**
     * Outputs the patient data to a file specified by the label.
     *
     * @param patientId The ID of the patient.
     * @param timeStamp The time the data was recorded.
     * @param label The type of data being recorded.
     * @param data The data to be stored.
     */
    @Override
    public void output(int patientId, long timeStamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the filePath variable
        // Changed variable name from FilePath, variables use lowerCamelCase
        // Change: seperated the code into two lines for better readability
        String filePath = fileMap.computeIfAbsent(label,
                k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                // Change: seperated the code into two lines for better readability
                Files.newBufferedWriter(Paths.get(filePath),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            // Change: seperated the code into two lines for better readability
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId, timeStamp, label, data);
        } catch (IOException e) { // Changed from generic Exception
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}