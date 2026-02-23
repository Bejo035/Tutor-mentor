package ge.batumi.tutormentor.controller.admin;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.model.response.UserResponse;
import ge.batumi.tutormentor.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin REST controller for managing users (CRUD and confirmation).
 */
@RestController
@RequestMapping("api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class UserAdminController {
    private final UserService userService;
    private final ProgramSchemeManager programSchemeManager;

    /**
     * Get all users from database
     *
     * @return List of {@link UserResponse} object.
     */
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserDb> userPage = userService.findAll(PageRequest.of(page, size));
        List<UserResponse> responses = programSchemeManager.getAllAsUserResponse(userPage.getContent());
        return ResponseEntity.ok(new PageImpl<>(responses, userPage.getPageable(), userPage.getTotalElements()));
    }

    /**
     * Creates a new user.
     */
    @PostMapping
    public UserResponse addUser(@Valid @RequestBody UserRequest request) {
        return programSchemeManager.getAsUserResponse(userService.addUser(request));
    }

    /**
     * Updates an existing user by ID.
     */
    @PutMapping("{id}")
    public UserResponse updateUser(@PathVariable String id, @Valid @RequestBody UserRequest request) throws ResourceNotFoundException {
        return programSchemeManager.getAsUserResponse(userService.updateUser(id, request));
    }

    /**
     * Deletes a user by ID.
     */
    @DeleteMapping("{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "Success";
    }

    /**
     * Confirms a user's account by ID.
     */
    @PatchMapping("{id}/confirm")
    public ResponseEntity<?> confirmUser(@PathVariable String id) throws ResourceNotFoundException {
        return ResponseEntity.ok(Map.of("message", userService.confirmUser(id) ? "Confirmed" : "User is already confirmed."));
    }

}
