package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.UserFileDb;
import ge.batumi.tutormentor.repository.UserFilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing {@link UserFileDb} entities that link users to GridFS files.
 */
@Service
public class UserFileService extends ARepositoryService<UserFilesRepository, UserFileDb, String> {
    private final ResourceService resourceService;

    public UserFileService(UserFilesRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    /**
     * Finds file entries for a specific user and file key.
     */
    public List<UserFileDb> findByUserIdAndKey(String id, String key) {
        return repository.findByUserIdAndKey(id, key);
    }

    /**
     * Deletes the given file entries and their underlying GridFS resources.
     */
    public void deleteAll(List<UserFileDb> userFileDbList) {
        userFileDbList.forEach(userFileDb -> {
            resourceService.deleteResourceById(userFileDb.getFileId());
            repository.delete(userFileDb);
        });
    }

    /**
     * Persists all given file entries.
     */
    public void saveAll(Iterable<UserFileDb> userFileDbListToSave) {
        repository.saveAll(userFileDbListToSave);
    }

    /**
     * Returns all file entries associated with the given user ID.
     */
    public List<UserFileDb> findAllByUserId(String id) {
        return repository.findAllByUserId(id);
    }
}
