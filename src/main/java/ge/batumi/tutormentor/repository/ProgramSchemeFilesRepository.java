package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.ProgramSchemeFileDb;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProgramSchemeFilesRepository extends MongoRepository<ProgramSchemeFileDb, String> {

    List<ProgramSchemeFileDb> findAllByProgramSchemeId(String userId);

    List<ProgramSchemeFileDb> findByProgramSchemeIdAndKey(String userId, String key);
}