package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.request.LoginRequest;
import ge.batumi.tutormentor.model.request.RefreshRequest;
import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.response.AuthResponse;
import ge.batumi.tutormentor.services.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PermitAll
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> register(@Valid @RequestPart("data") RegisterRequest request,
                                                 @RequestParam MultiValueMap<String, MultipartFile> files
    ) throws BadRequestException {
        return ResponseEntity.ok(authService.register(request, files));
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @RequestBody(required = false) RefreshRequest refreshRequest) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        String refreshToken = refreshRequest != null ? refreshRequest.getRefreshToken() : null;
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
