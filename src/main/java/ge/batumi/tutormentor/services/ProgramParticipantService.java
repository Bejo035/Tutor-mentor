package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.CourseParticipant;
import ge.batumi.tutormentor.model.db.ParticipantStatus;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.repository.ProgramParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing {@link CourseParticipant} records (course enrollment).
 */
@Service
public class ProgramParticipantService extends ARepositoryService<ProgramParticipantRepository, CourseParticipant, String> {

    ProgramParticipantService(ProgramParticipantRepository repository) {
        super(repository);
    }

    /**
     * Enrolls a user into a course with the given role.
     *
     * @param userId   the user to enroll.
     * @param courseId the target course.
     * @param role     the role the user will have in the course.
     * @return the created {@link CourseParticipant}.
     * @throws RuntimeException if the user is already enrolled in the course.
     */
    public CourseParticipant enroll(
            String userId,
            String courseId,
            UserProgramRole role
    ) {
        repository.findByUserIdAndCourseId(userId, courseId)
                .ifPresent(p -> {
                    throw new RuntimeException("User already enrolled");
                });

        CourseParticipant participant = new CourseParticipant();
        participant.setUserId(userId);
        participant.setCourseId(courseId);
        participant.setRole(role);
        participant.setStatus(ParticipantStatus.PENDING);

        return repository.save(participant);
    }

    /**
     * Returns all course participation records for the given user.
     */
    public List<CourseParticipant> getCoursesOfUser(String userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Returns all participation records for the given course.
     */
    public List<CourseParticipant> getUsersOfCourse(String courseId) {
        return repository.findByCourseId(courseId);
    }

    /**
     * Returns participation records for a course filtered by role.
     */
    public List<CourseParticipant> getUsersByRole(
            String courseId,
            UserProgramRole role
    ) {
        return repository.findByCourseIdAndRole(courseId, role);
    }
}