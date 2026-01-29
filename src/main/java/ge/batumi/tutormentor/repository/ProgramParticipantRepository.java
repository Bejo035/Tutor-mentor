package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.CourseParticipant;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramParticipantRepository extends MongoRepository<CourseParticipant, String> {

    List<CourseParticipant> findByUserId(String userId);

    List<CourseParticipant> findByCourseId(String courseId);

    List<CourseParticipant> findByCourseIdAndRole(String courseId, UserProgramRole role);

    Optional<CourseParticipant> findByUserIdAndCourseId(String userId, String courseId);
}