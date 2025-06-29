package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.ProgramScheme;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.repository.ProgramSchemeRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service layer for managing ProgramScheme operations.
 */
@Service
public class ProgramSchemeService extends ARepositoryService<ProgramSchemeRepository, ProgramScheme, String> {

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
    public ProgramScheme createProgramScheme(ProgramSchemeRequest request) {
        ProgramScheme program = new ProgramScheme(request.getTitle(), request.getDescription());
        return repository.save(program);
    }

    /**
     * Updates an existing ProgramScheme with new data.
     *
     * @param id      The ID of the ProgramScheme to update.
     * @param request The new request data.
     * @return The updated ProgramScheme entity.
     */
    public ProgramScheme updateProgramScheme(String id, ProgramSchemeRequest request) throws ResourceNotFoundException {
        ProgramScheme existing = repository.findById(id)
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
    public ProgramScheme getProgramScheme(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramScheme not found"));
    }

    /**
     * Retrieves full user details (UserDb objects) for mentors, tutors, and seekers of a ProgramScheme.
     *
     * @param id The ID of the ProgramScheme.
     * @return A map containing lists of users grouped by their role in the scheme.
     */
    public Map<String, List<UserDb>> getFullUserDetails(String id) throws ResourceNotFoundException {
        ProgramScheme scheme = getProgramScheme(id);
        Map<UserProgramRole, List<String>> userProgramRoleToUserMap = scheme.getUserProgramRoleToUserMap();
        Map<String, List<UserDb>> result = new HashMap<>();
        if (userProgramRoleToUserMap == null) {
            return result;
        }
        for (Map.Entry<UserProgramRole, List<String>> entry : userProgramRoleToUserMap.entrySet()) {
            result.put(entry.getKey().getName(), userService.findAllById(entry.getValue()));
        }

        return result;
    }

    /**
     * Retrieve all programScheme
     *
     * @return The corresponding {@link List<ProgramScheme>}
     */
    public List<ProgramScheme> getAll() {
        return repository.findAll();
    }

    public ProgramScheme save(ProgramScheme programScheme) {
        return repository.save(programScheme);
    }
}
