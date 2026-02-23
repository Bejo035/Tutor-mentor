package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link ProgramSchemeDb} entities.
 */
public interface ProgramSchemeRepository extends MongoRepository<ProgramSchemeDb, String> {
}
