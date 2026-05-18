package com.data_management;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import com.alerts.AlertGenerator;
import com.data_management.dataReaders.DataReader;
import com.data_management.dataReaders.FileDataReader;
import com.data_management.dataReaders.MockReader;
import com.data_management.dataReaders.WebSocketDataReader;
import com.data_management.patients.Patient;
import com.data_management.patients.PatientRecord;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private static DataStorage instance;

    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.
    private DataReader dataReader;

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage(DataReader dataReader) {
        this.patientMap = new ConcurrentHashMap<>();
        this.dataReader = dataReader;
    }

    /**
     * Returns the singleton instance of {@code DataStorage}, creating it on the first call.
     *
     * @param dataReader the {@link DataReader} to use when creating the instance for the first time
     * @return the shared {@code DataStorage} instance
     */
    public static DataStorage getInstance(DataReader dataReader) {
        if (instance == null) {
            instance = new DataStorage(dataReader);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of {@code DataStorage}, creating it on the first call with null dataReader.
     *
     * @return the shared {@code DataStorage} instance
     */
    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage(null);
        }
        return instance;
    }

    /**
     * Resets the singleton instance to {@code null}.
     * <p><strong>For testing purposes only.</strong></p>
     */
    // For testing purposes only
    public static void resetInstance() {
        instance = null;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage atomically to support concurrent real-time data updates.
     * Otherwise, the new data is appended to the existing patient's records
     * without duplicating information.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        patientMap.computeIfAbsent(patientId, id -> new Patient(id));
        patientMap.get(patientId).addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system by reading patient data from the specified output
     * directory, then continuously monitors and evaluates patient data to check
     * for alert conditions.
     *
     * <p>The output directory must be passed as the first command-line argument.
     * Each {@code .txt} file in that directory is read by {@link FileDataReader}
     * and parsed into {@link PatientRecord} objects stored in this instance.
     * If no directory is provided, the user is prompted to enter one via the
     * console.</p>
     *
     * <p>Optionally, a WebSocket connection can be established by passing a host
     * and port as the second and third arguments. The WebSocket connection is
     * non-blocking and receives data asynchronously in the background while the
     * rest of the system continues.</p>
     *
     * <p>Usage:</p>
     * <ul>
     *   <li>File only: {@code DataStorage <output-directory>}</li>
     *   <li>File + WebSocket: {@code DataStorage <output-directory> <host> <port>}</li>
     * </ul>
     *
     * @param args command-line arguments; {@code args[0]} is the path to the
     *             directory containing the simulator's output files,
     *             {@code args[1]} is the optional WebSocket host, and
     *             {@code args[2]} is the optional WebSocket port
     */
    public static void main(String[] args) {
        String outputDir;
        if (args.length < 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("No output directory provided. Please enter the path: ");
            outputDir = scanner.nextLine().trim();
        } else {
            outputDir = args[0];
        }

        DataReader reader = new FileDataReader(outputDir);
        DataStorage storage = DataStorage.getInstance(reader);

        try {
            reader.readData(storage);
        } catch (Exception e) {
            System.err.println("Failed to read file data: " + e.getMessage());
            e.printStackTrace();
        }

        // Optionally connect to a WebSocket server if host and port are provided
        // Usage: DataStorage <output-directory> <host> <port>
        if (args.length == 3) {
            String host = args[1];
            int port = Integer.parseInt(args[2]);
            WebSocketDataReader wsReader = new WebSocketDataReader(host, port);
            try {
                wsReader.readData(storage);
                System.out.println("WebSocket connection established to " + host + ":" + port);
            } catch (Exception e) {
                System.err.println("Failed to connect to WebSocket: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Example of using DataStorage to retrieve and print records for a patient
        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);
        for (PatientRecord record : records) {
            System.out.println("Record for Patient ID: " + record.getPatientId() +
                    ", Type: " + record.getRecordType() +
                    ", Data: " + record.getMeasurementValue() +
                    ", Timestamp: " + record.getTimestamp());
        }

        // Initialize the AlertGenerator with the storage
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        // Evaluate all patients' data to check for conditions that may trigger alerts
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }
    }
}