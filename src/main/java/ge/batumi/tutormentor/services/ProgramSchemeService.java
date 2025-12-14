package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.repository.ProgramSchemeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;


/**
 * Service layer for managing ProgramScheme operations.
 */
@Service
public class ProgramSchemeService extends ARepositoryService<ProgramSchemeRepository, ProgramSchemeDb, String> {

    private static final Logger LOGGER = LogManager.getLogger(ProgramSchemeService.class);
    private final UserService userService;

    @Autowired
    public ProgramSchemeService(ProgramSchemeRepository repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    /**
     * Creates and saves a new ProgramScheme.
     *
     * @param request The request object containing title, description, and user IDs.
     * @return The saved ProgramScheme entity.
     */
    public ProgramSchemeDb createProgramScheme(ProgramSchemeRequest request, Principal principal) {
        LOGGER.info("Creating new program Scheme");
        ProgramSchemeDb program = new ProgramSchemeDb(request);
        UserDb userDb = userService.loadUserByUsername(principal.getName());
        program.setCreatorUserId(userDb.getId());
        return repository.save(program);
    }

    /**
     * Updates an existing ProgramScheme with new data.
     *
     * @param id      The ID of the ProgramScheme to update.
     * @param request The new request data.
     * @return The updated ProgramScheme entity.
     */
    public ProgramSchemeDb updateProgramScheme(String id, ProgramSchemeRequest request) throws ResourceNotFoundException {
        ProgramSchemeDb existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramScheme not found"));
        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setUserProgramRoleToUserMap(new HashMap<>());

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

    public ProgramSchemeDb save(ProgramSchemeDb programSchemeDb) {
        return repository.save(programSchemeDb);
    }

    public List<ProgramSchemeDb> findAllById(List<String> userIds) {
        return repository.findAllById(userIds);
    }

}
