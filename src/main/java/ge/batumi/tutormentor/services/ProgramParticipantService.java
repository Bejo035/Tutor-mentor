package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.ParticipantStatus;
import ge.batumi.tutormentor.model.db.ProgramParticipant;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.repository.ProgramParticipantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramParticipantService extends ARepositoryService<ProgramParticipantRepository, ProgramParticipant, String> {

    ProgramParticipantService(ProgramParticipantRepository repository) {
        super(repository);
    }

    public ProgramParticipant enroll(
            String userId,
            String programId,
            UserProgramRole role
    ) {
        repository.findByUserIdAndProgramId(userId, programId)
                .ifPresent(p -> {
                    throw new RuntimeException("User already enrolled");
                });

        ProgramParticipant participant = new ProgramParticipant();
        participant.setUserId(userId);
        participant.setProgramId(programId);
        participant.setRole(role);
        participant.setStatus(ParticipantStatus.PENDING);

        return repository.save(participant);
    }

    public List<ProgramParticipant> getProgramsOfUser(String userId) {
        return repository.findByUserId(userId);
    }

    public List<ProgramParticipant> getUsersOfProgram(String programId) {
        return repository.findByProgramId(programId);
    }

    public List<ProgramParticipant> getUsersByRole(
            String programId,
            UserProgramRole role
    ) {
        return repository.findByProgramIdAndRole(programId, role);
    }
}