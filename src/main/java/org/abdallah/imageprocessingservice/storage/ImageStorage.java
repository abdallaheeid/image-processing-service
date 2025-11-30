package org.abdallah.imageprocessingservice.storage;

public interface ImageStorage {
    String save(byte[] data, String fileName) throws Exception;
    byte[] load(String fileName) throws Exception;
    void delete(String fileName) throws Exception;
}

