package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.ProgramScheme;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.repository.ProgramSchemeRepository;
import ge.batumi.tutormentor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service layer for managing ProgramScheme operations.
 */
@Service
@RequiredArgsConstructor
public class ProgramSchemeService {

    private static final Logger LOGGER = LogManager.getLogger(ProgramSchemeService.class);

    private final ProgramSchemeRepository programSchemeRepository;
    private final UserRepository userRepository;

    /**
     * Creates and saves a new ProgramScheme.
     *
     * @param request The request object containing title, description, and user IDs.
     * @return The saved ProgramScheme entity.
     */
    public ProgramScheme createProgramScheme(ProgramSchemeRequest request) throws BadRequestException {
        validateUserIds(request);
        ProgramScheme program = new ProgramScheme(
                null,
                request.getTitle(),
                request.getDescription(),
                request.getMentorIds(),
                request.getTutorIds(),
                request.getSeekerIds()
        );
        return programSchemeRepository.save(program);
    }

    /**
     * Updates an existing ProgramScheme with new data.
     *
     * @param id      The ID of the ProgramScheme to update.
     * @param request The new request data.
     * @return The updated ProgramScheme entity.
     */
    public ProgramScheme updateProgramScheme(String id, ProgramSchemeRequest request) throws BadRequestException {
        ProgramScheme existing = programSchemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProgramScheme not found"));

        validateUserIds(request);

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setMentorIds(request.getMentorIds());
        existing.setTutorIds(request.getTutorIds());
        existing.setSeekerIds(request.getSeekerIds());

        return programSchemeRepository.save(existing);
    }

    /**
     * Retrieves a ProgramScheme by its ID.
     *
     * @param id The ID of the ProgramScheme.
     * @return The corresponding ProgramScheme.
     */
    public ProgramScheme getProgramScheme(String id) {
        return programSchemeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProgramScheme not found"));
    }

    /**
     * Retrieves full user details (UserDb objects) for mentors, tutors, and seekers of a ProgramScheme.
     *
     * @param id The ID of the ProgramScheme.
     * @return A map containing lists of users grouped by their role in the scheme.
     */
    public Map<String, List<UserDb>> getFullUserDetails(String id) {
        ProgramScheme scheme = getProgramScheme(id);
        Map<String, List<UserDb>> result = new HashMap<>();
        result.put("mentors", userRepository.findAllById(scheme.getMentorIds()));
        result.put("tutors", userRepository.findAllById(scheme.getTutorIds()));
        result.put("seekers", userRepository.findAllById(scheme.getSeekerIds()));
        return result;
    }

    /**
     * Retrieve all programScheme
     *
     * @return The corresponding {@link List<ProgramScheme>}
     */
    public List<ProgramScheme> getAll() {
        return programSchemeRepository.findAll();
    }

    /**
     * Validates that all user IDs in the request exist in the database.
     *
     * @param request The ProgramScheme request containing user ID lists.
     */
    private void validateUserIds(ProgramSchemeRequest request) throws BadRequestException {
        List<String> allIds = new ArrayList<>();
        allIds.addAll(request.getMentorIds());
        allIds.addAll(request.getTutorIds());
        allIds.addAll(request.getSeekerIds());
        allIds = allIds.stream().distinct().toList();
        LOGGER.info(allIds);
        long countByIdIn = userRepository.countByIdIn(allIds);
        if (countByIdIn != allIds.size()) {
            throw new BadRequestException("Some user IDs are invalid.");
        }
    }
}
