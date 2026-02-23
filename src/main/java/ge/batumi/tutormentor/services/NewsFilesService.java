package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.NewsFileDb;
import ge.batumi.tutormentor.repository.NewsFilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing {@link NewsFileDb} entities that link news items to GridFS files.
 */
@Service
public class NewsFilesService extends ARepositoryService<NewsFilesRepository, NewsFileDb, String> {
    private final ResourceService resourceService;

    public NewsFilesService(NewsFilesRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    /**
     * Finds file entries for a specific news item and file key.
     */
    public List<NewsFileDb> findByNewsIdAndKey(String id, String key) {
        return repository.findByNewsIdAndKey(id, key);
    }

    /**
     * Deletes the given file entries and their underlying GridFS resources.
     */
    public void deleteAll(List<NewsFileDb> userFileDbList) {
        userFileDbList.forEach(userFileDb -> {
            resourceService.deleteResourceById(userFileDb.getFileId());
            repository.delete(userFileDb);
        });
    }

    /**
     * Persists all given file entries.
     */
    public void saveAll(Iterable<NewsFileDb> userFileDbListToSave) {
        repository.saveAll(userFileDbListToSave);
    }

    /**
     * Returns all file entries associated with the given news ID.
     */
    public List<NewsFileDb> findAllByNewsId(String id) {
        return repository.findAllByNewsId(id);
    }
}
