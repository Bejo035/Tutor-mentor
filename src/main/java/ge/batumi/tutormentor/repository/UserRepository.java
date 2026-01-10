package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<UserDb, String> {
    UserDb findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<UserDb> findAllByProgramRolesContains(UserProgramRole pro);
}
