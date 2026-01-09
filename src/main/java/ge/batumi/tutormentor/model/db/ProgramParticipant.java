package ge.batumi.tutormentor.model.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "program_participants")
public class ProgramParticipant {
    @Id
    private String id;

    private String userId;
    private String programId;

    private UserProgramRole role;
    private ParticipantStatus status = ParticipantStatus.PENDING;
    private LocalDateTime joinedAt = LocalDateTime.now();
}
