package ge.batumi.tutormentor.controller.admin;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.model.response.ProgramSchemeFullResponse;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller for managing ProgramScheme endpoints.
 */
@RestController
@RequestMapping("api/v1/admin/programScheme")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ProgramSchemeAdminController {
    private final ProgramSchemeService programSchemeService;
    private final ProgramSchemeManager programSchemeManager;

    /**
     * Creates a new ProgramScheme.
     *
     * @param request The ProgramScheme data.
     * @return The created ProgramScheme.
     */
    @PostMapping
    public ResponseEntity<ProgramSchemeDb> create(@RequestBody ProgramSchemeRequest request) {
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
    public ResponseEntity<ProgramSchemeDb> update(
            @PathVariable String id,
            @RequestBody ProgramSchemeRequest request) throws ResourceNotFoundException {
        return ResponseEntity.ok(programSchemeService.updateProgramScheme(id, request));
    }

    /**
     * Retrieves all ProgramScheme.
     *
     * @return The corresponding ProgramScheme.
     */
    @GetMapping
    public ResponseEntity<List<ProgramSchemeResponse>> getAll() {
        return ResponseEntity.ok(programSchemeManager.getAllAsProgramSchemeResponse(programSchemeService.findAll()));
    }

    /**
     * Retrieves a ProgramScheme by ID.
     *
     * @param id The ID of the ProgramScheme.
     * @return The corresponding ProgramScheme.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProgramSchemeFullResponse> get(@PathVariable String id) throws ResourceNotFoundException {
        return ResponseEntity.ok(programSchemeManager.getAsProgramSchemeFullResponse(programSchemeService.getProgramScheme(id)));
    }
}
