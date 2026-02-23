package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for {@link UserDb} entities with username/email lookups.
 */
@Repository
public interface UserRepository extends MongoRepository<UserDb, String> {
    UserDb findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
    Page<UserDb> findByProgramRolesIn(List<UserProgramRole> roles, Pageable pageable);
}
