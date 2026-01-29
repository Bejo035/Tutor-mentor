package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository interface for {@link Course} entity operations.
 * Provides CRUD operations and custom query methods for courses.
 */
public interface CourseRepository extends MongoRepository<Course, String> {

    /**
     * Finds all courses belonging to a specific program scheme.
     *
     * @param programId The program scheme ID.
     * @return List of courses for the given program.
     */
    List<Course> findByProgramId(String programId);

    /**
     * Finds all courses created by a specific user.
     *
     * @param creatorUserId The creator user ID.
     * @return List of courses created by the user.
     */
    List<Course> findByCreatorUserId(String creatorUserId);
}
