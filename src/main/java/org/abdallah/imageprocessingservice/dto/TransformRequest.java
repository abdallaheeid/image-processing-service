package org.abdallah.imageprocessingservice.dto;

import lombok.Data;

@Data
public class TransformRequest {

    private Resize resize;
    private Crop crop;
    private Integer rotate;
    private String format;
    private Filters filters;

    @Data
    public static class Resize {
        private Integer width;
        private Integer height;
    }

    @Data
    public static class Crop {
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;
    }

    @Data
    public static class Filters {
        private Boolean grayscale;
        private Boolean sepia;
    }
}
