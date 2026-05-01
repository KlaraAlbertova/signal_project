package com.data_management;

public class DataParser {

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
