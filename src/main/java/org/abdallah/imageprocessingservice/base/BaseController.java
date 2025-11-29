package org.abdallah.imageprocessingservice.base;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.NonNull;

@Transactional
public abstract class BaseController<T> {

    @Resource
    private EntityManagerFactory entityManagerFactory;

    protected final BaseRepository<T> repository;

    protected BaseController(BaseRepository<T> repository) {
        this.repository = repository;
    }

    @Operation(summary = "All <T>s to return")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<@NonNull Page<@NonNull T>> getAll(Pageable page) {
        return ResponseEntity.ok(repository.findAll(page));
    }

}
