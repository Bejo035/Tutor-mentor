package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}