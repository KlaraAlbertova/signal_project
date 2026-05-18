package com.data_management;

public class DataParser {

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

    private void parseTxt(String data, DataStorage dataStorage) {
        try {
            String[] parts = data.split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].replace("Patient ID:", "").trim());
                long timestamp = Long.parseLong(parts[1].replace("Timestamp:", "").trim());
                String recordType = parts[2].replace("Label:", "").trim();
                double measurementValue = parseValue(parts[3].replace("Data:", "").trim());

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        } catch (NumberFormatException e) {
            System.err.println("Skipping wrongly formatted line: " + data);
        }
    }

    private void parseWebSocket(String data, DataStorage dataStorage) {
        try {
            String[] parts = data.trim().split(",");
            if (parts.length == 4) {
                int patientId = Integer.parseInt(parts[0].trim());
                double measurementValue = parseValue(parts[1].trim());
                String recordType = parts[2].trim();
                long timestamp = Long.parseLong(parts[3].trim());

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            }
        } catch (NumberFormatException e) {
            System.err.println("Skipping wrongly formatted line: " + data);
        }
    }

    private double parseValue(String raw) {
        String v = raw.trim().replace("%", "");
        if (v.equalsIgnoreCase("triggered")) return 1.0;
        if (v.equalsIgnoreCase("resolved"))  return 0.0;
        return Double.parseDouble(v);
    }
}