package ge.batumi.tutormentor.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for a program scheme including its associated courses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSchemeFullResponse {
    private String id;
    private String title;
    private String description;
    private UserData creatorUserData;
    private List<CourseResponse> courses;
}
