package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.UserDb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<UserDb, String> {
    long countByIdIn(List<String> idList);
}
