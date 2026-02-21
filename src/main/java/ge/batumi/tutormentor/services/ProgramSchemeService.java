package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.db.ProgramSchemeFileDb;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.repository.ProgramSchemeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


/**
 * Service layer for managing ProgramScheme operations.
 */
@Service
public class ProgramSchemeService extends ARepositoryService<ProgramSchemeRepository, ProgramSchemeDb, String> {

    private static final Logger LOGGER = LogManager.getLogger(ProgramSchemeService.class);
    private final UserService userService;
    private final ProgramSchemeFileService programSchemeFileService;
    private final ResourceService resourceService;

    @Autowired
    public ProgramSchemeService(ProgramSchemeRepository repository, UserService userService, ProgramSchemeFileService programSchemeFileService, ResourceService resourceService) {
        super(repository);
        this.userService = userService;
        this.programSchemeFileService = programSchemeFileService;
        this.resourceService = resourceService;
    }

    /**
     * Creates and saves a new ProgramScheme.
     *
     * @param request The request object containing title, description, and user IDs.
     * @return The saved ProgramScheme entity.
     */
    public ProgramSchemeDb createProgramScheme(ProgramSchemeRequest request, MultiValueMap<String, MultipartFile> files, Principal principal) {
        LOGGER.info("Creating new program Scheme");
        ProgramSchemeDb program = new ProgramSchemeDb(request);
        UserDb userDb = userService.loadUserByUsername(principal.getName());
        program.setCreatorUserId(userDb.getId());
        program = repository.save(program);
        if (files != null && !files.isEmpty()) {
            updateProgramSchemeFiles(files, program);
        }

        return program;
    }

    private void updateProgramSchemeFiles(MultiValueMap<String, MultipartFile> files, ProgramSchemeDb program) {
        List<ProgramSchemeFileDb> programSchemeFileDbListToSave = new ArrayList<>();

        files.forEach((key, multipartFiles) -> {
            List<ProgramSchemeFileDb> programSchemeFileDbList = programSchemeFileService.findByProgramSchemeIdAndKey(program.getId(), key);
            if (!programSchemeFileDbList.isEmpty()) {
                programSchemeFileService.deleteAll(programSchemeFileDbList);
            }
            programSchemeFileDbListToSave.addAll(getNewProgramSchemeFileDbList(key, multipartFiles));
        });
        programSchemeFileDbListToSave.forEach(userFileDb -> userFileDb.setProgramSchemeId(program.getId()));
        programSchemeFileService.saveAll(programSchemeFileDbListToSave);
    }

    private List<ProgramSchemeFileDb> getNewProgramSchemeFileDbList(String key, List<MultipartFile> multipartFileList) {
        List<ProgramSchemeFileDb> userFileDbListToSave = new ArrayList<>();
        multipartFileList.forEach(file -> {
            try {
                ObjectId fileId = resourceService.uploadFile(file);
                userFileDbListToSave.add(new ProgramSchemeFileDb(fileId.toString(), key));
            } catch (IOException e) {
                LOGGER.warn("Error while uploading file to database.");
            }
        });
        return userFileDbListToSave;
    }


    /**
     * Updates an existing ProgramScheme with new data.
     *
     * @param id      The ID of the ProgramScheme to update.
     * @param request The new request data.
     * @return The updated ProgramScheme entity.
     */
    public ProgramSchemeDb updateProgramScheme(String id, ProgramSchemeRequest request, MultiValueMap<String, MultipartFile> files) throws ResourceNotFoundException {
        ProgramSchemeDb existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramScheme not found"));
        if (request != null) {
            existing.setTitle(request.getTitle());
            existing.setDescription(request.getDescription());
        }
        if (files != null && !files.isEmpty()) {
            updateProgramSchemeFiles(files, existing);
        }
        return repository.save(existing);
    }

    /**
     * Retrieves a ProgramScheme by its ID.
     *
     * @param id The ID of the ProgramScheme.
     * @return The corresponding ProgramScheme.
     */
    public ProgramSchemeDb getProgramScheme(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramScheme not found"));
    }
}
