package org.abdallah.imageprocessingservice.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    public String save(MultipartFile file, String storedFileName) throws IOException {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);

        Path target = root.resolve(storedFileName);
        file.transferTo(target);
        return target.toString();
    }

    public String saveBytes(byte[] bytes, String storedFileName) throws IOException {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root);

        Path target = root.resolve(storedFileName).normalize();

        // Use a real output stream
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(target))) {
            bos.write(bytes);
        }

        return target.toFile().getAbsolutePath();  // always return absolute, safe path
    }

    public Resource loadAsResource(String storagePath) {
        return new FileSystemResource(storagePath);
    }
}
