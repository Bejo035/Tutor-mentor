package ge.batumi.tutormentor.model.response;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
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
    private Integer maxSize;
    private ProgramSchemeDb.RegistrationDates registrationDates;
    private UserData creatorUserData;
}
