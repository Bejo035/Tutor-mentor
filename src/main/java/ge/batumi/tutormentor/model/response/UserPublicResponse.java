package ge.batumi.tutormentor.model.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * Limited user response DTO for public-facing endpoints (e.g. mentors list).
 * Excludes sensitive fields like email, username, and roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicResponse {
    private String id;
    private String name;
    private String surname;
    private List<UserProgramRole> programRoles;
    private String mentoringCourseName;
    private String courseDescription;
    private Integer rating;
    @JsonIgnore
    private MultiValueMap<String, String> keyToFileIdsMap = new LinkedMultiValueMap<>();

    @JsonAnyGetter
    public MultiValueMap<String, String> getFlattenedMap() {
        return keyToFileIdsMap;
    }
}
