package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.request.UpdateUserRequest;
import ge.batumi.tutormentor.model.response.UserResponse;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

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
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        return ResponseEntity.ok(userService.loadUserByUsername(principal.getName()).toUserResponse());
    }

    @PutMapping(value = "me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> meUpdate(@RequestPart(value = "data", required = false) UpdateUserRequest request,
                                                 @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
                                                 @RequestPart(value = "cv", required = false) MultipartFile cv, Principal principal) throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok(userService.updateUser(principal, request, profilePhoto, cv).toUserResponse());
    }

    @GetMapping("mentors")
    public ResponseEntity<List<UserResponse>> getMentors() {
        return ResponseEntity.ok(userService.getMentorsAndTutors());
    }
}