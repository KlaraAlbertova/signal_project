package com.data_management;

/**
 * Parses raw patient data strings into structured records and stores them
 * in a {@link DataStorage} instance.
 *
 * <p>Currently supports TXT and WebSocket-formatted input.
 * Additional formats may be added.</p>
 */
public class DataParser {

    /**
     * Parses the given data string according to the specified format and stores
     * the resulting record in the provided {@link DataStorage}.
     *
     * <p>If any of the arguments are {@code null} or empty, the method returns
     * without performing any action. Unsupported formats are reported to
     * {@code System.err}.</p>
     *
     * <p>Supported formats:</p>
     * <ul>
     *   <li>{@code "TXT"} — a line in the format:
     *       {@code Patient ID: [id], Timestamp: [ts], Label: [label], Data: [value]}</li>
     *   <li>{@code "WS"} — a comma-separated line with four fields:
     *       patient ID, timestamp, record type, and measurement value</li>
     * </ul>
     *
     * @param data        the raw data string to parse
     * @param dataStorage the storage instance where the parsed record will be saved
     * @param format      the format of the data string (e.g., {@code "TXT"})
     */
    public void parse(String data, DataStorage dataStorage, String format) {
        if (data == null || data.isEmpty() || dataStorage == null || format == null || format.isEmpty()) return;

        switch (format) {
            case "TXT":
                parseTxt(data, dataStorage);
                break;
            case "WS":
                parseWebSocket(data, dataStorage);
                break;
            default:
                System.err.println("Unsupported format: " + format);
        }
    }

    /**
     * Parses a single TXT-formatted line written by {@link com.cardio_generator.outputs.FileOutputStrategy}
     * and stores the extracted record in the given {@link DataStorage}.
     *
     * <p>The expected format is:</p>
     * <pre>Patient ID: [id], Timestamp: [timestamp], Label: [label], Data: [value]</pre>
     *
     * <p>Lines that do not match this format or contain unparseable numeric values
     * are skipped and an error is printed to {@code System.err}.</p>
     *
     * @param data        the TXT line to parse
     * @param dataStorage the storage instance where the parsed record will be saved
     */
    private void parseTxt(String data, DataStorage dataStorage) {
        try {
            String[] parts = data.split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].replace("Patient ID:", "").trim());
                long timestamp = Long.parseLong(parts[1].replace("Timestamp:", "").trim());
                String recordType = parts[2].replace("Label:", "").trim();
                double measurementValue = Double.parseDouble(parts[3].replace("Data:", "").trim());

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        } catch (NumberFormatException e) {
            System.err.println("Skipping wrongly formatted line: " + data);
        }
    }

    /**
     * Parses a single WebSocket-formatted line and stores the extracted record
     * in the given {@link DataStorage}.
     *
     * <p>The expected format is exactly four comma-separated fields:</p>
     * <ol>
     *   <li>Patient ID (integer)</li>
     *   <li>Measurement value (double)</li>
     *   <li>Record type (String)</li>
     *   <li>Timestamp (long)</li>
     * </ol>
     *
     * <p>Lines that do not contain exactly four fields or contain unparseable
     * numeric values are skipped and an error is printed to {@code System.err}.</p>
     *
     * @param data        the WebSocket message to parse
     * @param dataStorage the storage instance where the parsed record will be saved
     */
    private void parseWebSocket(String data, DataStorage dataStorage) {
        try {
            String[] parts = data.trim().split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].trim());
                double measurementValue = Double.parseDouble(parts[1].trim());
                String recordType = parts[2].trim();
                long timestamp = Long.parseLong(parts[3].trim());

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        } catch (NumberFormatException e) {
            System.err.println("Skipping wrongly formatted line: " + data);
        }
    }
}
