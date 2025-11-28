package org.abdallah.imageprocessingservice.base;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
@Transactional
public interface BaseRepository<T> extends
        PagingAndSortingRepository<T, Long>,
        JpaRepository<T, Long>,
        JpaSpecificationExecutor<T> {

    T findByUuid(final String uid);
}
