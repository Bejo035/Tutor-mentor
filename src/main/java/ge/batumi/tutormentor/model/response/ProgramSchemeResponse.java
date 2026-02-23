package ge.batumi.tutormentor.model.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Response DTO for a program scheme summary with creator data and file references.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSchemeResponse {
    private String id;
    private String title;
    private String description;
    private UserData creatorUserData;
    @JsonIgnore
    private MultiValueMap<String, String> keyToFileIdsMap = new LinkedMultiValueMap<>();

    @JsonAnyGetter
    public MultiValueMap<String, String> getFlattenedMap() {
        return keyToFileIdsMap;
    }
}
