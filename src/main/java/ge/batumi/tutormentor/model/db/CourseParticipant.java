package ge.batumi.tutormentor.model.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * MongoDB document representing a user's enrollment in a course with a specific program role.
 */
@Data
@Document(collection = "course_participants")
public class CourseParticipant {
    @Id
    private String id;

    @Indexed
    private String userId;
    @Indexed
    private String courseId;

    private UserProgramRole role;
    private ParticipantStatus status = ParticipantStatus.PENDING;
    private LocalDateTime joinedAt = LocalDateTime.now();
}
