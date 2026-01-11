package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.request.LoginRequest;
import ge.batumi.tutormentor.model.request.RefreshRequest;
import ge.batumi.tutormentor.model.request.RegisterRequest;
import ge.batumi.tutormentor.model.response.AuthResponse;
import ge.batumi.tutormentor.services.AuthService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PermitAll
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestPart("data") RegisterRequest request,
                                                 @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
                                                 @RequestPart(value = "cv", required = false) MultipartFile cv
    ) throws BadRequestException {
        return ResponseEntity.ok(authService.register(request, profilePhoto, cv));
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
