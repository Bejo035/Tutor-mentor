package ge.batumi.tutormentor.model.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "course_participants")
public class CourseParticipant {
    @Id
    private String id;

    private String userId;
    private String courseId;

    private UserProgramRole role;
    private ParticipantStatus status = ParticipantStatus.PENDING;
    private LocalDateTime joinedAt = LocalDateTime.now();
}
