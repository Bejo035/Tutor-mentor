package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.CourseParticipant;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramParticipantRepository extends MongoRepository<CourseParticipant, String> {

    List<CourseParticipant> findByUserId(String userId);

    List<CourseParticipant> findByProgramId(String programId);

    List<CourseParticipant> findByProgramIdAndRole(String programId, UserProgramRole role);

    Optional<CourseParticipant> findByUserIdAndProgramId(String userId, String programId);
}