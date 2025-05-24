package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.ProgramScheme;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProgramSchemeRepository extends MongoRepository<ProgramScheme, String> {
}
