package ge.batumi.tutormentor.model.request;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Course name is required")
    private String name;
    /** The maximum number of participants allowed in the course. */
    @NotNull(message = "Max size is required")
    @Min(value = 1, message = "Max size must be at least 1")
    private Integer maxSize;
    /** The registration period for the course. */
    private ProgramSchemeDb.RegistrationDates registrationDates;
    /** The ID of the program scheme this course belongs to. */
    @NotBlank(message = "Program ID is required")
    private String programId;
}
