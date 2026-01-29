package ge.batumi.tutormentor.model.request;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a Course.
 * Contains all the necessary fields for course management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    /** The name of the course. */
    private String name;
    /** The maximum number of participants allowed in the course. */
    private Integer maxSize;
    /** The registration period for the course. */
    private ProgramSchemeDb.RegistrationDates registrationDates;
    /** The ID of the program scheme this course belongs to. */
    private String programId;
}
