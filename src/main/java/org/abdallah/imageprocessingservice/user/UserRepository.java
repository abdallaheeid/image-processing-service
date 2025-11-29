package org.abdallah.imageprocessingservice.user;

import org.abdallah.imageprocessingservice.base.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByUsername(String username);
}
