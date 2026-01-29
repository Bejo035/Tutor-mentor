package ge.batumi.tutormentor.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSchemeResponse {
    private String id;
    private String title;
    private String description;
    private UserData creatorUserData;
}
