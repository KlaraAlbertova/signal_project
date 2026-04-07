package com.data_management;

import java.io.IOException;

public class MockReader implements DataReader {
    private String path;
    public MockReader(String path) {
        this.path = path;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (dataStorage == null) {
            throw new IllegalArgumentException("DataStorage can not be null");
        }
    }
}