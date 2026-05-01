package com.data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileDataReader implements DataReader {
    private String outputDirectory;
    private DataParser dataParser = new DataParser();

    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

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
