package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("v1/programScheme")
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
    public ResponseEntity<Page<ProgramSchemeResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProgramSchemeDb> schemePage = programSchemeService.findAll(PageRequest.of(page, size));
        List<ProgramSchemeResponse> responses = programSchemeManager.getAllAsProgramSchemeResponse(schemePage.getContent());
        return ResponseEntity.ok(new PageImpl<>(responses, schemePage.getPageable(), schemePage.getTotalElements()));
    }
}