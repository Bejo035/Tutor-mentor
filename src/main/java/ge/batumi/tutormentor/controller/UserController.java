package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.services.UserService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle user related stuff.
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 24.06.2025
 */
@RestController
@RequestMapping("${request.mapping.prefix}/users")
@RequiredArgsConstructor
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
    public UserDb updateUser(@PathParam("id") String id, @RequestBody UserRequest request) throws ResourceNotFoundException {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("{id}")
    public String deleteUser(@PathParam("id") String id) throws ResourceNotFoundException {
        userService.deleteUser(id);
        return "Success";
    }

}