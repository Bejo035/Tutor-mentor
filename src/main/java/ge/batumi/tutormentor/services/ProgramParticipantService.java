package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.ParticipantStatus;
import ge.batumi.tutormentor.model.db.CourseParticipant;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.repository.ProgramParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramParticipantService extends ARepositoryService<ProgramParticipantRepository, CourseParticipant, String> {

    ProgramParticipantService(ProgramParticipantRepository repository) {
        super(repository);
    }

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

    public List<CourseParticipant> getCoursesOfUser(String userId) {
        return repository.findByUserId(userId);
    }

    public List<CourseParticipant> getUsersOfCourse(String courseId) {
        return repository.findByCourseId(courseId);
    }

    public List<CourseParticipant> getUsersByRole(
            String courseId,
            UserProgramRole role
    ) {
        return repository.findByCourseIdAndRole(courseId, role);
    }
}