package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProgramSchemeRepository extends MongoRepository<ProgramSchemeDb, String> {
}
