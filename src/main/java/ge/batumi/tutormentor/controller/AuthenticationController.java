package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.request.AuthRequest;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${request.mapping.prefix}/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationController.class);
    private final UserService userService;

    @PostMapping("login")
    public String login(@RequestBody AuthRequest request) {
        LOGGER.info(request);
        return userService.verify(request);
    }
}
