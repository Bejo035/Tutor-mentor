package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.request.UpdateUserRequest;
import ge.batumi.tutormentor.model.response.UserPublicResponse;
import ge.batumi.tutormentor.model.response.UserResponse;
import ge.batumi.tutormentor.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

/**
 * Controller class to handle user related stuff.
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 24.06.2025
 */
@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Returns the authenticated user's full profile.
     */
    @GetMapping("me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        return ResponseEntity.ok(userService.toUserResponse(userService.findByUsername(principal.getName())));
    }

    /**
     * Updates the authenticated user's profile and/or associated files.
     */
    @PutMapping(value = "me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> meUpdate(@Valid @RequestPart(value = "data", required = false) UpdateUserRequest request,
                                                 @RequestParam MultiValueMap<String, MultipartFile> files, Principal principal) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.toUserResponse(userService.updateUser(principal, request, files)));
    }

    /**
     * Returns paginated public profile data for all mentors and tutors.
     */
    @GetMapping("mentors")
    public ResponseEntity<Page<UserPublicResponse>> getMentors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getMentorsAndTutorsPublic(PageRequest.of(page, size)));
    }
}