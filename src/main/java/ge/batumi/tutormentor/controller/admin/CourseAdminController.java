package ge.batumi.tutormentor.controller.admin;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.Course;
import ge.batumi.tutormentor.model.request.CourseRequest;
import ge.batumi.tutormentor.model.response.CourseResponse;
import ge.batumi.tutormentor.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing Course endpoints (Admin only).
 */
@RestController
@RequestMapping("api/v1/admin/course")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CourseAdminController {
    private final CourseService courseService;
    private final ProgramSchemeManager programSchemeManager;

    /**
     * Creates a new Course.
     *
     * @param request   The Course data.
     * @param principal The authenticated user.
     * @return The created Course.
     */
    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request, Principal principal) {
        Course course = courseService.createCourse(request, principal);
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(List.of(course)).get(0));
    }

    /**
     * Updates an existing Course by ID.
     *
     * @param id      The ID of the Course.
     * @param request The updated Course data.
     * @return The updated Course.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CourseRequest request) throws ResourceNotFoundException {
        Course course = courseService.updateCourse(id, request);
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(List.of(course)).get(0));
    }

    /**
     * Retrieves all Courses.
     *
     * @return The list of CourseResponse objects.
     */
    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Course> coursePage = courseService.findAll(PageRequest.of(page, size));
        List<CourseResponse> responses = programSchemeManager.getAllAsCourseResponse(coursePage.getContent());
        return ResponseEntity.ok(new PageImpl<>(responses, coursePage.getPageable(), coursePage.getTotalElements()));
    }

    /**
     * Retrieves a Course by ID.
     *
     * @param id The ID of the Course.
     * @return The corresponding CourseResponse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> get(@PathVariable String id) throws ResourceNotFoundException {
        Course course = courseService.getCourse(id);
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(List.of(course)).get(0));
    }

    /**
     * Retrieves Courses by program scheme ID.
     *
     * @param programId The program scheme ID.
     * @return The list of CourseResponse objects for the given program.
     */
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<CourseResponse>> getByProgramId(@PathVariable String programId) {
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(courseService.getCoursesByProgramId(programId)));
    }
}
