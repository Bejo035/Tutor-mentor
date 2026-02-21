package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.NewsFileDb;
import ge.batumi.tutormentor.repository.NewsFilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsFilesService extends ARepositoryService<NewsFilesRepository, NewsFileDb, String> {
    private final ResourceService resourceService;

    public NewsFilesService(NewsFilesRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    public List<NewsFileDb> findByNewsIdAndKey(String id, String key) {
        return repository.findByNewsIdAndKey(id, key);
    }

    public void deleteAll(List<NewsFileDb> userFileDbList) {
        userFileDbList.forEach(userFileDb -> {
            resourceService.deleteResourceById(userFileDb.getFileId());
            repository.delete(userFileDb);
        });
    }

    public void saveAll(Iterable<NewsFileDb> userFileDbListToSave) {
        repository.saveAll(userFileDbListToSave);
    }

    public List<NewsFileDb> findAllByNewsId(String id) {
        return repository.findAllByNewsId(id);
    }
}
