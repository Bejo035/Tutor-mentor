package ge.batumi.tutormentor.repository;

import ge.batumi.tutormentor.model.db.ProgramParticipant;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramParticipantRepository extends MongoRepository<ProgramParticipant, String> {

    List<ProgramParticipant> findByUserId(String userId);

    List<ProgramParticipant> findByProgramId(String programId);

    List<ProgramParticipant> findByProgramIdAndRole(String programId, UserProgramRole role);

    Optional<ProgramParticipant> findByUserIdAndProgramId(String userId, String programId);
}