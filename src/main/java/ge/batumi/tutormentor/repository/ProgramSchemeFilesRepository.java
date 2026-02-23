package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.ProgramSchemeFileDb;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * MongoDB repository for {@link ProgramSchemeFileDb} entities with program-scheme-based lookups.
 */
public interface ProgramSchemeFilesRepository extends MongoRepository<ProgramSchemeFileDb, String> {

    List<ProgramSchemeFileDb> findAllByProgramSchemeId(String programId);

    List<ProgramSchemeFileDb> findByProgramSchemeIdAndKey(String programId, String key);
}