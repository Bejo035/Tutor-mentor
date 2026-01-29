package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.exceptions.ExpectationsNotMet;
import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller class to handle user related stuff.
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
     * Add user to {@link ProgramSchemeDb} as specified {@link UserProgramRole}.
     *
     * @param id                       programScheme unique identifier.
     * @param userProgramRoleToUserMap UserProgramRole to userId map to add.
     * @return Result {@link ProgramSchemeResponse} object
     * @throws ResourceNotFoundException If {@link ProgramSchemeResponse} could not be found.
     */
    @PutMapping("/{id}/addUser")
    public ResponseEntity<List<ProgramSchemeResponse>> addUsers(
            @PathVariable String id,
            @RequestBody Map<UserProgramRole, String> userProgramRoleToUserMap) throws ResourceNotFoundException, ExpectationsNotMet {
        Set<ProgramSchemeDb> resultSet = new HashSet<>();
        for (Map.Entry<UserProgramRole, String> entry : userProgramRoleToUserMap.entrySet()) {
            resultSet.add(programSchemeManager.addUserToProgramScheme(id, entry.getValue(), entry.getKey()));
        }

        return ResponseEntity.ok(programSchemeManager.getAllAsProgramSchemeResponse(resultSet.stream().toList()));
    }

    /**
     * Retrieves all ProgramScheme.
     *
     * @return The corresponding ProgramSchemeResponses.
     */
    @GetMapping
    public ResponseEntity<List<ProgramSchemeResponse>> getAll() {
        return ResponseEntity.ok(programSchemeManager.getAllAsProgramSchemeResponse(programSchemeService.findAll()));
    }

    @PreAuthorize("@security.hasProgramRole('TUTOR')")
    @GetMapping
    public ResponseEntity<?> asdasdasd(){

    }
}