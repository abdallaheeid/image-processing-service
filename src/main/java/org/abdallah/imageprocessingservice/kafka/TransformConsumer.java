package org.abdallah.imageprocessingservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.abdallah.imageprocessingservice.image.Image;
import org.abdallah.imageprocessingservice.image.ImageRepository;
import org.abdallah.imageprocessingservice.storage.StorageService;
import org.abdallah.imageprocessingservice.transformations.ImageTransformService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransformConsumer {

    private final ImageTransformService transformService;
    private final StorageService storageService;
    private final ImageRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "image.transform", groupId = "image-transform-group")
    public void listen(String message) throws Exception {

        TransformTask task = mapper.readValue(message, TransformTask.class);

        byte[] transformed = transformService.transform(
                task.getStoragePath(),
                task.getRequest(),
                task.getTargetFormat()
        );

        String newKey = storageService.saveBytes(transformed, task.getOutputFileName());

        Image img = new Image();
        img.setOriginalFileName(task.getOriginalFileName());
        img.setStoredFileName(task.getOutputFileName());
        img.setStoragePath(newKey);
        img.setFormat(task.getTargetFormat());
        img.setOwnerUsername(task.getOwner());
        img.setOriginalImage(false);
        img.setParentUuid(task.getUuid());

        repo.save(img);
    }
}

