package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.UserDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserDb, String> {
    UserDb findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
