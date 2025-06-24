package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${request.mapping.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDb> getAllUsers() {
        return userService.findAll();
    }
}