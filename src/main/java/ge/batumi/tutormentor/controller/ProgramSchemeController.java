package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle ProgramScheme related operations.
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 24.06.2025
 */
@RestController
@RequestMapping("api/v1/programScheme")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*")
public class ProgramSchemeController {
    private final ProgramSchemeService programSchemeService;
    private final ProgramSchemeManager programSchemeManager;

    /**
     * Retrieves all ProgramScheme.
     *
     * @return The corresponding ProgramSchemeResponses.
     */
    @GetMapping
    public ResponseEntity<List<ProgramSchemeResponse>> getAll() {
        return ResponseEntity.ok(programSchemeManager.getAllAsProgramSchemeResponse(programSchemeService.findAll()));
    }
}