package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

// Change: added Javadoc comment
/**
 * Implementation of {@link OutputStrategy} that writes health data to text files.
 * Creates the target directory structure if it does not exist and organizes
 * output into separate files per data label.
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
     * Constructs a {@code FileOutputStrategy} with the specified target directory.
     *
     * @param baseDirectory the root directory where output files will be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    // Change: added Javadoc comment
    /**
     * Writes patient data to a label-specific text file within the base directory.
     * The file is created if it does not exist, and data is appended in the format:
     * {@code Patient ID: [patientId], Timestamp: [timestamp], Label: [label], Data: [data]}.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the type of data being recorded; also used as the output filename
     * @param data      the data value to be written
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
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
                    patientId, timestamp, label, data);
        } catch (IOException e) { // Changed from generic Exception
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}