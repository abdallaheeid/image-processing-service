package org.abdallah.imageprocessingservice.image;


import lombok.NonNull;
import org.abdallah.imageprocessingservice.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends BaseRepository<Image> {
    Page<@NonNull Image> findByOwnerUsername(String ownerUsername, Pageable pageable);

    List<Image> findByParentUuid(String parentUuid);
}
