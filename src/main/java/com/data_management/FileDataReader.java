package com.data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads patient data from files in a specified directory and stores the parsed
 * records into a {@link DataStorage} instance.
 *
 * <p>Implements {@link DataReader} and uses a {@link DataParser} to interpret
 * each line according to its file format. Currently, supports {@code .csv} files;
 * lines from files with unrecognized extensions are passed with an empty format
 * string and will be skipped by the parser.</p>
 */
public class FileDataReader implements DataReader {
    private String outputDirectory;
    private DataParser dataParser = new DataParser();

    /**
     * Constructs a {@code FileDataReader} that reads from the given directory path.
     *
     * @param outputDirectory the path to the directory containing patient data files
     */
    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Recursively reads all regular files in the configured directory, parses each
     * line using a {@link DataParser}, and stores the resulting records in the given
     * {@link DataStorage}.
     *
     * <p>File format is inferred from the file extension:</p>
     * <ul>
     *   <li>{@code .csv} — parsed as CSV</li>
     *   <li>All other extensions — format is left empty and lines will be skipped
     *       by the parser</li>
     * </ul>
     *
     * @param dataStorage the storage instance where parsed records will be saved
     * @throws IOException if the configured path does not exist, is not a directory,
     *                     or an error occurs while reading a file
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        Path path = Paths.get(outputDirectory);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IOException("The path is not a valid directory: " + outputDirectory);
        }

        try (Stream<Path> walk = Files.walk(path)) {
            List<Path> files = walk.filter(Files::isRegularFile).collect(Collectors.toList());

            for (Path file : files) {
                String fileName = file.getFileName().toString().toLowerCase();
                String format = "";

                if (fileName.endsWith(".csv")) {
                    format = "CSV";
                }

                List<String> lines = Files.readAllLines(file);
                for (String line : lines) {
                    dataParser.parse(line, dataStorage, format);
                }
            }
        }
    }

}
