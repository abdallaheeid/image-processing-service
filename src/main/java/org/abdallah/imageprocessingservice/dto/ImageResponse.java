package org.abdallah.imageprocessingservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private String uuid;
    private String url;
    private String originalFileName;
    private String contentType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private String format;
    private boolean originalImage;
    private String parentUuid;
    private String transformations;
}