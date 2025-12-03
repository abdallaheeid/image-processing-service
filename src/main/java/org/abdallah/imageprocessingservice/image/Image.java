package org.abdallah.imageprocessingservice.image;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.abdallah.imageprocessingservice.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
public class Image extends BaseEntity {

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long sizeBytes;

    private Integer width;
    private Integer height;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String format;

    private boolean originalImage = true;

    private String parentUuid;

    @Column(length = 1000)
    private String transformations;

    @Column(nullable = false)
    private String ownerUsername;
}