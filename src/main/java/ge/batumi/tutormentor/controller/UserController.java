package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controller class to handle user related stuff.
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 24.06.2025
 */
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Get all users from database
     *
     * @return List of {@link UserDb} object.
     */
    @GetMapping
    public List<UserDb> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping
    public UserDb addUser(@RequestBody UserRequest request) {
        return userService.addUser(request);
    }

    @PutMapping("{id}")
    public UserDb updateUser(@PathVariable String id, @RequestBody UserRequest request) throws ResourceNotFoundException {
        return userService.updateUser(id, request);
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


    @GetMapping("me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDb> me(Principal principal) {
        return ResponseEntity.ok(userService.loadUserByUsername(principal.getName()));
    }
}