package ge.batumi.tutormentor.model.response;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Course data.
 * Contains course details along with creator information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    /** The unique identifier of the course. */
    private String id;
    /** The name of the course. */
    private String name;
    /** The maximum number of participants allowed in the course. */
    private Integer maxSize;
    /** The registration period for the course. */
    private ProgramSchemeDb.RegistrationDates registrationDates;
    /** The ID of the program scheme this course belongs to. */
    private String programId;
    /** Information about the user who created this course. */
    private UserData creatorUserData;
}
