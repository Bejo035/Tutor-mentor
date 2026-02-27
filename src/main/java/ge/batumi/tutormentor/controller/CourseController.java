package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.exceptions.ExpectationsNotMet;
import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.manager.ProgramSchemeManager;
import ge.batumi.tutormentor.model.db.Course;
import ge.batumi.tutormentor.model.db.UserProgramRole;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller class to handle Course related operations.
 */
@RestController
@RequestMapping("api/v1/course")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*")
public class CourseController {
    private final CourseService courseService;
    private final ProgramSchemeManager programSchemeManager;

    /**
     * Creates a new Course. Only accessible to users with MENTOR or TUTOR program role.
     *
     * @param request   The Course data.
     * @param principal The authenticated user.
     * @return The created Course.
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasProgramRole('MENTOR') or @securityExpression.hasProgramRole('TUTOR')")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request, Principal principal) {
        Course course = courseService.createCourse(request, principal);
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(List.of(course)).get(0));
    }

    /**
     * Retrieves all courses.
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
     * Retrieves courses by program scheme id.
     *
     * @param programId The program scheme id.
     * @return The list of CourseResponse objects for the given program.
     */
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<CourseResponse>> getByProgramId(@PathVariable String programId) {
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(courseService.getCoursesByProgramId(programId)));
    }

    /**
     * Retrieves a course by id.
     *
     * @param id The course id.
     * @return The CourseResponse object.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getById(@PathVariable String id) throws ResourceNotFoundException {
        Course course = courseService.getCourse(id);
        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(List.of(course)).get(0));
    }

    /**
     * Add user to Course as specified UserProgramRole.
     *
     * @param id                       Course unique identifier.
     * @param userProgramRoleToUserMap UserProgramRole to userId map to add.
     * @return Result list of CourseResponse objects.
     * @throws ResourceNotFoundException If Course could not be found.
     * @throws ExpectationsNotMet        If registration dates are not valid.
     */
    @PutMapping("/{id}/addUser")
    public ResponseEntity<List<CourseResponse>> addUsers(
            @PathVariable String id,
            @RequestBody Map<UserProgramRole, String> userProgramRoleToUserMap) throws ResourceNotFoundException, ExpectationsNotMet {
        Set<Course> resultSet = new HashSet<>();
        for (Map.Entry<UserProgramRole, String> entry : userProgramRoleToUserMap.entrySet()) {
            resultSet.add(programSchemeManager.addUserToCourse(id, entry.getValue(), entry.getKey()));
        }

        return ResponseEntity.ok(programSchemeManager.getAllAsCourseResponse(resultSet.stream().toList()));
    }
}
