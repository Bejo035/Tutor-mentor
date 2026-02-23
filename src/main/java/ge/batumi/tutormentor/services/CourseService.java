package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.Course;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.request.CourseRequest;
import ge.batumi.tutormentor.repository.CourseRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

/**
 * Service layer for managing {@link Course} operations.
 * Provides business logic for course creation, retrieval, and updates.
 */
@Service
public class CourseService extends ARepositoryService<CourseRepository, Course, String> {

    private static final Logger LOGGER = LogManager.getLogger(CourseService.class);
    private final UserService userService;

    public CourseService(CourseRepository repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    /**
     * Creates and saves a new Course.
     *
     * @param request   The request object containing course details.
     * @param principal The authenticated user creating the course.
     * @return The saved Course entity.
     */
    public Course createCourse(CourseRequest request, Principal principal) {
        LOGGER.info("Creating new course for program: {}", request.getProgramId());
        Course course = new Course();
        course.setName(request.getName());
        course.setMaxSize(request.getMaxSize());
        course.setRegistrationDates(request.getRegistrationDates());
        course.setProgramId(request.getProgramId());

        UserDb userDb = userService.findByUsername(principal.getName());
        course.setCreatorUserId(userDb.getId());

        return repository.save(course);
    }

    /**
     * Updates an existing Course with new data.
     *
     * @param id      The ID of the Course to update.
     * @param request The new course data.
     * @return The updated Course entity.
     * @throws ResourceNotFoundException If the course is not found.
     */
    public Course updateCourse(String id, CourseRequest request) throws ResourceNotFoundException {
        Course existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        existing.setName(request.getName());
        existing.setMaxSize(request.getMaxSize());
        existing.setRegistrationDates(request.getRegistrationDates());
        existing.setProgramId(request.getProgramId());

        return repository.save(existing);
    }

    /**
     * Retrieves a Course by its ID.
     *
     * @param id The ID of the Course.
     * @return The corresponding Course.
     * @throws ResourceNotFoundException If the course is not found.
     */
    public Course getCourse(String id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    /**
     * Retrieves all courses belonging to a specific program scheme.
     *
     * @param programId The program scheme ID.
     * @return List of courses for the given program.
     */
    public List<Course> getCoursesByProgramId(String programId) {
        return repository.findByProgramId(programId);
    }

    /**
     * Saves a Course entity.
     *
     * @param course The Course to save.
     * @return The saved Course entity.
     */
    public Course save(Course course) {
        return repository.save(course);
    }

    /**
     * Retrieves multiple courses by their IDs.
     *
     * @param courseIds List of course IDs.
     * @return List of courses matching the given IDs.
     */
    public List<Course> findAllById(List<String> courseIds) {
        return repository.findAllById(courseIds);
    }
}
