package ge.batumi.tutormentor.model.response;

import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSchemeFullResponse {
    private String id;
    private String title;
    private String description;
    private Integer maxSize;
    private ProgramSchemeDb.RegistrationDates registrationDates;
    private UserData creatorUserData;
    private Map<UserProgramRole, List<UserData>> userProgramRoleToUserMap;
}
