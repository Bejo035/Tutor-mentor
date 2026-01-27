package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.UserFileDb;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserFilesRepository extends MongoRepository<UserFileDb, String> {

    List<UserFileDb> findAllByUserId(String userId);

    List<UserFileDb> findByUserIdAndKey(String userId, String key);
}