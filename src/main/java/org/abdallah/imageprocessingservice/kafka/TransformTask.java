package org.abdallah.imageprocessingservice.kafka;

import lombok.Data;
import org.abdallah.imageprocessingservice.dto.TransformRequest;

@Data
public class TransformTask {
    private String uuid;
    private String storagePath;
    private TransformRequest request;
    private String targetFormat;
    private String outputFileName;
    private String originalFileName;
    private String owner;
}

