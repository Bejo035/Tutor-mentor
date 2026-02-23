package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.BadRequestException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.LoginRequest;
import ge.batumi.tutormentor.model.request.RefreshRequest;
import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.response.AuthResponse;
import ge.batumi.tutormentor.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service handling user authentication, registration, token refresh, and logout.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger LOGGER = LogManager.getLogger(AuthService.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Authenticates a user by username and password.
     *
     * @param request the login credentials.
     * @return access and refresh tokens.
     * @throws BadRequestException if the credentials are invalid.
     */
    public AuthResponse login(LoginRequest request) {
        UserDb userDb = userService.findByUsername(request.getUsername());
        if (userDb == null || !passwordEncoder.matches(request.getPassword(), userDb.getPassword())) {
            throw new BadRequestException("Incorrect username or password.");
        }

        return generateResponse(userDb);
    }

    /**
     * Registers a new user and optionally uploads profile files.
     *
     * @param request the registration data.
     * @param files   optional files to associate with the new user.
     * @return access and refresh tokens for the newly registered user.
     * @throws BadRequestException if the email or username is already taken.
     */
    @Transactional // requires MongoDB replica set
    public AuthResponse register(RegisterRequest request, MultiValueMap<String, MultipartFile> files) {
        if (userService.existsByEmail(request.getEmail()) || userService.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Email or username is taken.");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        UserDb userDb = new UserDb(request);
        userDb = userService.save(userDb);
        if (files != null && !files.isEmpty()) {
            userService.updateUserFiles(files, userDb);
        }
        LOGGER.info("User registered by '{}' id", userDb.getId());

        return generateResponse(userDb);
    }

    private AuthResponse generateResponse(UserDb userDetails) {
        return new AuthResponse(jwtService.generateAccessToken(userDetails), jwtService.generateRefreshToken(userDetails));
    }

    /**
     * Rotates a refresh token: blacklists the old one and issues a new token pair.
     *
     * @param request contains the current refresh token.
     * @return new access and refresh tokens.
     * @throws BadCredentialsException if the refresh token is invalid.
     */
    public AuthResponse refresh(RefreshRequest request) {
        if (jwtService.isRefreshTokenValid(request.getRefreshToken())) {
            // Blacklist the old refresh token
            String jti = jwtService.getJtiFromToken(request.getRefreshToken());
            if (jti != null) {
                tokenBlacklistService.blacklist(jti, jwtService.getExpirationFromToken(request.getRefreshToken()));
            }

            String username = jwtService.getUsernameFromRefreshToken(request.getRefreshToken());
            UserDb userDb = userService.findByUsername(username);
            return generateResponse(userDb);
        }
        throw new BadCredentialsException("Invalid refreshToken");
    }

    /**
     * Blacklists the provided access and/or refresh tokens to invalidate them.
     *
     * @param accessToken  the access token to revoke, or {@code null}.
     * @param refreshToken the refresh token to revoke, or {@code null}.
     */
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) {
            String jti = jwtService.getJtiFromToken(accessToken);
            if (jti != null) {
                tokenBlacklistService.blacklist(jti, jwtService.getExpirationFromToken(accessToken));
            }
        }
        if (refreshToken != null) {
            String jti = jwtService.getJtiFromToken(refreshToken);
            if (jti != null) {
                tokenBlacklistService.blacklist(jti, jwtService.getExpirationFromToken(refreshToken));
            }
        }
    }
}
