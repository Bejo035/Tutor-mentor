package ge.batumi.tutormentor.controller.admin;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.model.response.UserFullResponse;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * @return List of {@link UserFullResponse} object.
     */
    @GetMapping
    public List<UserFullResponse> getAllUsers() {
        return programSchemeManager.getAllAsUserFullResponse(userService.findAll());
    }

    @PostMapping
    public UserFullResponse addUser(@RequestBody UserRequest request) {
        return programSchemeManager.getAsUserFullResponse(userService.addUser(request));
    }

    @PutMapping("{id}")
    public UserFullResponse updateUser(@PathVariable String id, @RequestBody UserRequest request) throws ResourceNotFoundException {
        return programSchemeManager.getAsUserFullResponse(userService.updateUser(id, request));
    }

    @DeleteMapping("{id}")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "Success";
    }

    @PatchMapping("{id}/confirm")
    public ResponseEntity<?> confirmUser(@PathVariable String id) throws ResourceNotFoundException {
        return ResponseEntity.ok(Map.of("message", userService.confirmUser(id) ? "Confirmed" : "User is already confirmed."));
    }

}
