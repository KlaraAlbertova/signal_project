package com.data_management;

/**
 * Parses raw patient data strings into structured records and stores them
 * in a {@link DataStorage} instance.
 *
 * <p>Currently supports CSV-formatted input. Additional formats may be added.</p>
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
     *   <li>{@code "CSV"} — a comma-separated line with four fields:
     *       patient ID, measurement value, record type, and timestamp</li>
     * </ul>
     *
     * @param data        the raw data string to parse
     * @param dataStorage the storage instance where the parsed record will be saved
     * @param format      the format of the data string (e.g., {@code "CSV"})
     */
    public void parse(String data, DataStorage dataStorage, String format) {
        if (data == null || data.isEmpty() || dataStorage==null || format==null || format.isEmpty()) return;

        switch (format) {
            case "CSV":
                parseCsv(data, dataStorage);
                break;
            default:
                System.err.println("Unsupported format: " + format);
        }
    }

    /**
     * Parses a single CSV-formatted line and stores the extracted record in the given
     * {@link DataStorage}.
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
     * @param data        the CSV line to parse
     * @param dataStorage the storage instance where the parsed record will be saved
     */
    private void parseCsv(String data, DataStorage dataStorage) {
        try {
            String[] parts = data.split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].trim());
                double measurementValue = Double.parseDouble(parts[1].trim());
                String recordType = parts[2].trim();
                long timestamp = Long.parseLong(parts[3].trim());

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        } catch (NumberFormatException e) {
            System.err.println("Skipping wrongly formated line: " + data);
        }
    }
}
