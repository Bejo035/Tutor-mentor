package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.db.ProgramScheme;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing ProgramScheme endpoints.
 */
@RestController
@RequestMapping("${request.mapping.prefix}/programScheme")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor

public class ProgramSchemeController {

    private final ProgramSchemeService programSchemeService;

    /**
     * Creates a new ProgramScheme.
     *
     * @param request The ProgramScheme data.
     * @return The created ProgramScheme.
     */
    @PostMapping
    public ResponseEntity<ProgramScheme> create(@RequestBody ProgramSchemeRequest request) throws BadRequestException {
        return ResponseEntity.ok(programSchemeService.createProgramScheme(request));
    }

    /**
     * Updates an existing ProgramScheme by ID.
     *
     * @param id      The ID of the ProgramScheme.
     * @param request The updated ProgramScheme data.
     * @return The updated ProgramScheme.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProgramScheme> update(
            @PathVariable String id,
            @RequestBody ProgramSchemeRequest request) throws BadRequestException {
        return ResponseEntity.ok(programSchemeService.updateProgramScheme(id, request));
    }

    /**
     * Retrieves all ProgramScheme.
     *
     * @return The corresponding ProgramScheme.
     */
    @GetMapping
    public ResponseEntity<List<ProgramScheme>> getAll() {
        return ResponseEntity.ok(programSchemeService.getAll());
    }

    /**
     * Retrieves a ProgramScheme by ID.
     *
     * @param id The ID of the ProgramScheme.
     * @return The corresponding ProgramScheme.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgramScheme> get(@PathVariable String id) {
        return ResponseEntity.ok(programSchemeService.getProgramScheme(id));
    }

    /**
     * Retrieves full user details for mentors, tutors, and seekers of a ProgramScheme.
     *
     * @param id The ID of the ProgramScheme.
     * @return A map containing lists of UserDb objects by role.
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<Map<String, List<UserDb>>> getFullUsers(@PathVariable String id) {
        return ResponseEntity.ok(programSchemeService.getFullUserDetails(id));
    }
}
