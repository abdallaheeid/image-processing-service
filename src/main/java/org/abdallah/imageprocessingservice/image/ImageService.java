package org.abdallah.imageprocessingservice.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.abdallah.imageprocessingservice.dto.ImageResponse;
import org.abdallah.imageprocessingservice.dto.TransformRequest;
import org.abdallah.imageprocessingservice.kafka.TransformProducer;
import org.abdallah.imageprocessingservice.kafka.TransformTask;
import org.abdallah.imageprocessingservice.storage.StorageService;
import org.abdallah.imageprocessingservice.transformations.ImageTransformService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository repository;
    private final StorageService storageService;
    private final ImageTransformService transformService;
    private final TransformProducer producer;
    private final ObjectMapper mapper = new ObjectMapper();

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        return auth.getName();
    }

    public ImageResponse upload(MultipartFile file) throws IOException {
        String owner = currentUsername();
        String ext = detectExtension(file.getOriginalFilename());

        Image image = new Image();
        String uuid = image.getUuid();
        String storedFileName = uuid + "." + ext;

        // Read image locally (do NOT load from S3)
        BufferedImage img = ImageIO.read(file.getInputStream());
        int width = img.getWidth();
        int height = img.getHeight();

        String storagePath = storageService.save(file, storedFileName);

        image.setOriginalFileName(file.getOriginalFilename());
        image.setStoredFileName(storedFileName);
        image.setContentType(file.getContentType());
        image.setSizeBytes(file.getSize());
        image.setWidth(width);
        image.setHeight(height);
        image.setStoragePath(storagePath);
        image.setFormat(ext);
        image.setOriginalImage(true);
        image.setOwnerUsername(owner);

        Image saved = repository.save(image);

        return toResponse(saved);
    }

    @Cacheable(value="images", key = "#uuid")
    public Resource loadImageResource(String uuid) {
        Image image = repository.findByUuid(uuid);
        return storageService.loadAsResource(image.getStoragePath());
    }

    public Page<ImageResponse> listImages(Pageable pageable) {
        String owner = currentUsername();
        return repository.findByOwnerUsername(owner, pageable).map(this::toResponse);
    }

    public ImageResponse transform(String uuid, TransformRequest request) throws IOException {
        Image original = repository.findByUuid(uuid);
        if (original == null) {
            throw new RuntimeException("Image not found");
        }

        String targetFormat = request.getFormat() != null ? request.getFormat() : original.getFormat();
        String newUuid = UUID.randomUUID().toString();
        String storedFileName = newUuid + "." + targetFormat;

        byte[] transformedBytes = transformService.transform(original.getStoragePath(), request, targetFormat);
        String storagePath = storageService.saveBytes(transformedBytes, storedFileName);

        BufferedImage img = ImageIO.read(new File(storagePath));
        int width = img.getWidth();
        int height = img.getHeight();

        Image transformed = new Image();
        transformed.setOriginalFileName(original.getOriginalFileName());
        transformed.setStoredFileName(storedFileName);
        transformed.setContentType("image/" + targetFormat);
        transformed.setSizeBytes((long) transformedBytes.length);
        transformed.setWidth(width);
        transformed.setHeight(height);
        transformed.setStoragePath(storagePath);
        transformed.setFormat(targetFormat);
        transformed.setOriginalImage(false);
        transformed.setParentUuid(original.getUuid());
        transformed.setOwnerUsername(original.getOwnerUsername());
        transformed.setTransformations(request.toString());

        Image saved = repository.save(transformed);
        return toResponse(saved);
    }

    private String detectExtension(String name) {
        if (name == null || !name.contains(".")) return "png";
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

    public ImageResponse toResponse(Image image) {
        return ImageResponse.builder()
                .uuid(image.getUuid())
                .url("/api/images/" + image.getUuid())
                .originalFileName(image.getOriginalFileName())
                .contentType(image.getContentType())
                .sizeBytes(image.getSizeBytes())
                .width(image.getWidth())
                .height(image.getHeight())
                .format(image.getFormat())
                .originalImage(image.isOriginalImage())
                .parentUuid(image.getParentUuid())
                .transformations(image.getTransformations())
                .build();
    }

    public String queueTransform(String uuid, TransformRequest request) throws Exception {

        Image img = repository.findByUuid(uuid);

        String newFileName = UUID.randomUUID().toString() + "." +
                (request.getFormat() != null ? request.getFormat() : img.getFormat());

        TransformTask task = new TransformTask();
        task.setUuid(uuid);
        task.setStoragePath(img.getStoragePath());
        task.setRequest(request);
        task.setTargetFormat(request.getFormat());
        task.setOutputFileName(newFileName);
        task.setOriginalFileName(img.getOriginalFileName());
        task.setOwner(img.getOwnerUsername());

        String json = mapper.writeValueAsString(task);

        producer.sendTransformEvent(json);

        return "queued";
    }

    public List<Image> findChildren(String parentUuid) {
        return repository.findByParentUuid(parentUuid);
    }
}
