package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.ProgramSchemeFileDb;
import ge.batumi.tutormentor.repository.ProgramSchemeFilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing {@link ProgramSchemeFileDb} entities that link program schemes to GridFS files.
 */
@Service
public class ProgramSchemeFileService extends ARepositoryService<ProgramSchemeFilesRepository, ProgramSchemeFileDb, String> {
    private final ResourceService resourceService;

    public ProgramSchemeFileService(ProgramSchemeFilesRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    /**
     * Finds file entries for a specific program scheme and file key.
     */
    public List<ProgramSchemeFileDb> findByProgramSchemeIdAndKey(String id, String key) {
        return repository.findByProgramSchemeIdAndKey(id, key);
    }

    /**
     * Deletes the given file entries and their underlying GridFS resources.
     */
    public void deleteAll(List<ProgramSchemeFileDb> userFileDbList) {
        userFileDbList.forEach(userFileDb -> {
            resourceService.deleteResourceById(userFileDb.getFileId());
            repository.delete(userFileDb);
        });
    }

    /**
     * Persists all given file entries.
     */
    public void saveAll(Iterable<ProgramSchemeFileDb> userFileDbListToSave) {
        repository.saveAll(userFileDbListToSave);
    }

    /**
     * Returns all file entries associated with the given program scheme ID.
     */
    public List<ProgramSchemeFileDb> findAllByProgramSchemeId(String id) {
        return repository.findAllByProgramSchemeId(id);
    }
}
