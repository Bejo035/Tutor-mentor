package ge.batumi.tutormentor.controller.admin;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.request.ProgramSchemeRequest;
import ge.batumi.tutormentor.model.response.ProgramSchemeFullResponse;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProgramSchemeResponse> create(@Valid @RequestPart("data") ProgramSchemeRequest request, @RequestParam MultiValueMap<String, MultipartFile> files, Principal principal) {
        return ResponseEntity.ok(programSchemeManager.getProgramSchemeResponse(programSchemeService.createProgramScheme(request, files, principal)));
    }

    /**
     * Updates an existing ProgramScheme by ID.
     *
     * @param id      The ID of the ProgramScheme.
     * @param request The updated ProgramScheme data.
     * @return The updated ProgramScheme.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProgramSchemeResponse> update(
            @PathVariable String id,
            @Valid @RequestPart(value = "data", required = false) ProgramSchemeRequest request,
            @RequestParam MultiValueMap<String, MultipartFile> files) throws ResourceNotFoundException {
        return ResponseEntity.ok(programSchemeManager.getProgramSchemeResponse(programSchemeService.updateProgramScheme(id, request, files)));
    }

    /**
     * Retrieves all ProgramScheme.
     *
     * @return The corresponding ProgramScheme.
     */
    @GetMapping
    public ResponseEntity<Page<ProgramSchemeResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProgramSchemeDb> schemePage = programSchemeService.findAll(PageRequest.of(page, size));
        List<ProgramSchemeResponse> responses = programSchemeManager.getAllAsProgramSchemeResponse(schemePage.getContent());
        return ResponseEntity.ok(new PageImpl<>(responses, schemePage.getPageable(), schemePage.getTotalElements()));
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
