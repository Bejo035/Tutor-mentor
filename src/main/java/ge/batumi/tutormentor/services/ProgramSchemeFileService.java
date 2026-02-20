package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.ProgramSchemeFileDb;
import ge.batumi.tutormentor.repository.ProgramSchemeFilesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramSchemeFileService extends ARepositoryService<ProgramSchemeFilesRepository, ProgramSchemeFileDb, String> {
    private final ResourceService resourceService;

    public ProgramSchemeFileService(ProgramSchemeFilesRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    public List<ProgramSchemeFileDb> findByProgramSchemeIdAndKey(String id, String key) {
        return repository.findByProgramSchemeIdAndKey(id, key);
    }

    public void deleteAll(List<ProgramSchemeFileDb> userFileDbList) {
        userFileDbList.forEach(userFileDb -> {
            resourceService.deleteResourceById(userFileDb.getFileId());
            repository.delete(userFileDb);
        });
    }

    public void saveAll(Iterable<ProgramSchemeFileDb> userFileDbListToSave) {
        repository.saveAll(userFileDbListToSave);
    }

    public List<ProgramSchemeFileDb> findAllByProgramSchemeId(String id) {
        return repository.findAllByProgramSchemeId(id);
    }
}
