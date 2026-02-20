package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.UserFileDb;
import ge.batumi.tutormentor.repository.UserFilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFileService extends ARepositoryService<UserFilesRepository, UserFileDb, String> {
    private final ResourceService resourceService;

    public UserFileService(UserFilesRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    public List<UserFileDb> findByUserIdAndKey(String id, String key) {
        return repository.findByUserIdAndKey(id, key);
    }

    public void deleteAll(List<UserFileDb> userFileDbList) {
        userFileDbList.forEach(userFileDb -> {
            resourceService.deleteResourceById(userFileDb.getFileId());
            repository.delete(userFileDb);
        });
    }

    public void saveAll(Iterable<UserFileDb> userFileDbListToSave) {
        repository.saveAll(userFileDbListToSave);
    }

    public List<UserFileDb> findAllByUserId(String id) {
        return repository.findAllByUserId(id);
    }
}
