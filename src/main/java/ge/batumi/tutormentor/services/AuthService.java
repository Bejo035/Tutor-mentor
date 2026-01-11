package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.LoginRequest;
import ge.batumi.tutormentor.model.request.RefreshRequest;
import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.response.AuthResponse;
import ge.batumi.tutormentor.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger LOGGER = LogManager.getLogger(AuthService.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) throws BadRequestException {
        UserDb userDb = userService.loadUserByUsername(request.getUsername());
        if (userDb == null || !passwordEncoder.matches(request.getPassword(), userDb.getPassword())) {
            throw new BadRequestException("Incorrect username or password.");
        }

        return generateResponse(userDb);
    }

    public AuthResponse register(RegisterRequest request, MultipartFile profilePhoto, MultipartFile cv) throws BadRequestException {
        if (userService.existsByEmail(request.getEmail()) || userService.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Email or username is taken.");
        }

        UserDb userDb = new UserDb(request);
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            userService.updateProfilePhoto(profilePhoto, userDb);
        }
        if (cv != null && !cv.isEmpty()) {
            userService.updateCv(cv, userDb);
        }
        userDb = userService.save(userDb);
        LOGGER.info("User registered by '{}' id", userDb.getId());

        return generateResponse(userDb);
    }

    private AuthResponse generateResponse(UserDb userDetails) {
        return new AuthResponse(jwtService.generateAccessToken(userDetails), jwtService.generateRefreshToken(userDetails));
    }

    public AuthResponse refresh(RefreshRequest request) {
        if (jwtService.isRefreshTokenValid(request.getRefreshToken())) {
            String username = jwtService.getUsernameFromRefreshToken(request.getRefreshToken());
            UserDb userDb = userService.loadUserByUsername(username);
            return generateResponse(userDb);
        }
        throw new BadCredentialsException("Invalid refreshToken");
    }
}
